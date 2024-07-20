package fplhn.udpm.examdistribution.core.headsubject.examrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.examrule.repository.ERSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.examrule.service.ExamRuleService;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExamRuleServiceImpl implements ExamRuleService {

    private final ERSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final SessionHelper sessionHelper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String semesterId = sessionHelper.getCurrentSemesterId();
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(pageable, departmentFacilityId, semesterId, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> uploadExamRule(String subjectId, UploadExamRuleRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Nội quy thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Nội quy thi không được lớn hơn 5MB"
                );
            }

            Subject putSubject = subjectRepository.getReferenceById(subjectId);

            if (putSubject.getPathExamRule() != null) {
                googleDriveFileService.deleteById(putSubject.getPathExamRule());
            }

            String folderName = "ExamRule/" + putSubject.getSubjectCode();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);
            String fileId = googleDriveFileDTO.getId();
            putSubject.setPathExamRule(fileId);
            subjectRepository.save(putSubject);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Tải nội quy thi thành công"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi trong quá trình xử lý"
            );
        }
    }

    @Override
    public ResponseObject<?> getFile(GetFileRequest request) {
        try {
            Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
            if (isSubjectExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này!"
                );
            }

            Subject subject = isSubjectExist.get();
            if (subject.getPathExamRule() == null || subject.getPathExamRule().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Môn học này chưa được tải quy định đề thi"
                );
            }

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_DETAIL_EXAM_RULE + request.getSubjectId();

            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "success"
                );
            }
            Resource fileResponse = googleDriveFileService.loadFile(request.getFileId());
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new FileResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "success"
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    "Quy định thi không tồn tại",
                    HttpStatus.OK,
                    "success"
            );
        }

    }

};
