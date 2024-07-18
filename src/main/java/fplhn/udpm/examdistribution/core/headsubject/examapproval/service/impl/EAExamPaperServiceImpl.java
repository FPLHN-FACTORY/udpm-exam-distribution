package fplhn.udpm.examdistribution.core.headsubject.examapproval.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamApprovalRequest;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EAExamPaperCleanAfterSeventDayResponse;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.repository.EAExamPaperRepository;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.repository.EASubjectRepository;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.service.EAExamPaperService;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EAExamPaperServiceImpl implements EAExamPaperService {

    private final Long SEVEN_DAY = 604800000L;

    private final EAExamPaperRepository examApprovalRepository;

    private final EASubjectRepository subjectRepository;

    private final HttpSession httpSession;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    @Override
    public ResponseObject<?> getExamApprovals(EAExamPaperRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String semesterId = httpSession.getAttribute(SessionConstant.CURRENT_SEMESTER_ID).toString();
        return new ResponseObject<>(
                examApprovalRepository.getExamApprovals(
                        pageable, request, httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString(),
                        semesterId
                ),
                HttpStatus.OK,
                "Lấy danh sách đề thi cần phê duyệt thành công"
        );
    }

    @Override
    public ResponseObject<?> getSubjects(String departmentFacilityId, String staffId) {
        String semesterId = httpSession.getAttribute(SessionConstant.CURRENT_SEMESTER_ID).toString();
        return new ResponseObject<>(
                subjectRepository.getAllSubjects(
                        departmentFacilityId, staffId, semesterId
                ),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getFile(String fileId) {
        try {
            if (fileId.trim().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Bạn chưa tải lên file"
                );
            }

            Optional<ExamPaper> examPaper = examApprovalRepository.findByPath(fileId);

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER_APPROVAL + examPaper.get().getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Tìm thấy đề thi thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(fileId);
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new FileResponse(data, resource.getFilename()),
                    HttpStatus.OK,
                    "Tìm thấy đề thi thành công"
            );
        } catch (IOException e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đề thi không tồn tại"
            );
        }
    }

    @Override
    public ResponseObject<?> approvalExam(EAExamApprovalRequest request) {
        Optional<ExamPaper> examPaper = examApprovalRepository.findById(request.getExamPaperId());
        if (examPaper.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Đề không tồn tại"
            );
        }
        examPaper.get().setExamPaperStatus(ExamPaperStatus.IN_USE);
        examPaper.get().setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
        examApprovalRepository.save(examPaper.get());
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Phê duyệt thành công"
        );
    }

    @Override
    public ResponseObject<?> deleteExamPaper(String examPaperId) {
        Optional<ExamPaper> examPaper = examApprovalRepository.findById(examPaperId);
        if (examPaper.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Đề không tồn tại"
            );
        }
        String fileId = examPaper.get().getPath();

        examApprovalRepository.deleteById(examPaper.get().getId());

        try {
            googleDriveFileService.deleteById(fileId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Xóa đề thành công"
        );
    }

    @Override
    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanExamPaper() {
        List<EAExamPaperCleanAfterSeventDayResponse> examPapers = examApprovalRepository.findAllExamPaperStatusAndCreatedDate(SEVEN_DAY, new Date().getTime());
        for (EAExamPaperCleanAfterSeventDayResponse examPaper : examPapers) {
            try {
                examApprovalRepository.deleteById(examPaper.getId());
                googleDriveFileService.deleteById(examPaper.getPath());
            } catch (Exception e) {
            }
        }
    }


}
