package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPUploadExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UploadExamPaperServiceImpl implements UploadExamPaperService {

    private final UEPUploadExamPaperExtendRepository examPaperRepository;

    private final UEPMajorFacilityExtendRepository majorFacilityRepository;

    private final UEPSubjectExtendRepository subjectRepository;

    private final UEPStaffExtendRepository staffRepository;

    private final UEPBlockExtendRepository blockRepository;

    private final UEPClassSubjectExtendRepository classSubjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final EmailService emailService;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getListCurrentSubject() {
        String userId = sessionHelper.getCurrentUserId();

        String semesterId = sessionHelper.getCurrentSemesterId();
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        return new ResponseObject<>(
                subjectRepository.getListSubject(userId, departmentFacilityId, semesterId),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getListStaff() {
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        return new ResponseObject<>(
                staffRepository.getListStaff(departmentFacilityId),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllExamPaper(ListExamPaperRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String userId = sessionHelper.getCurrentUserId();
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        String semesterId = sessionHelper.getCurrentSemesterId();
        return new ResponseObject<>(
                PageableObject.of(examPaperRepository.getListExamPaper(pageable, request, userId, departmentFacilityId, semesterId, ExamPaperStatus.WAITING_APPROVAL.toString())),
                HttpStatus.OK,
                "Lấy thành công danh sách đề thi"
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

            Optional<ExamPaper> examPaper = examPaperRepository.findByPath(fileId);

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER + examPaper.get().getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Tìm thấy file thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(fileId);
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new FileResponse(data, resource.getFilename()),
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đề thi không tồn tại"
            );
        }
    }

    @Override
    public ResponseObject<?> deleteExamPaper(String examPaperId) {
        try {
            Optional<ExamPaper> isExamPaperExist = examPaperRepository.findById(examPaperId);
            if (isExamPaperExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            } else {
                ExamPaper examPaper = isExamPaperExist.get();
                examPaper.setExamPaperStatus(
                        examPaper.getExamPaperStatus().equals(ExamPaperStatus.IN_USE) ? ExamPaperStatus.STOP_USING : ExamPaperStatus.IN_USE
                );
                examPaperRepository.save(examPaper);
                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        "Thay đổi trạng thái của đề thi thành công"
                );
            }
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> getListMajorFacility() {
        try {
            String majorFacilityId = sessionHelper.getCurrentUserMajorFacilityId();
            String semesterId = sessionHelper.getCurrentSemesterId();
            String staffId = sessionHelper.getCurrentUserId();
            return new ResponseObject<>(
                    examPaperRepository.getMajorFacilityByDepartmentFacilityId(majorFacilityId, staffId, semesterId),
                    HttpStatus.OK,
                    "Lấy thành công danh sách chuyên ngành cơ sở"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> createExamPaper(@Valid CreateExamPaperRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Đề thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Nội quy thi không được lớn hơn 5MB"
                );
            }

            Optional<MajorFacility> isMajorFacilityExist = majorFacilityRepository.findById(request.getMajorFacilityId());
            if (isMajorFacilityExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chuyên ngành cơ sở này"
                );
            }

            Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
            if (isSubjectExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            ExamPaper postExamPaper = new ExamPaper();

            Long now = new Date().getTime();
            boolean isFoundBlock = false;
            for (Block block : blockRepository.findAll()) {
                if (block.getStartTime() <= now && now <= block.getEndTime()) {
                    postExamPaper.setBlock(block);
                    isFoundBlock = true;
                    break;
                }
            }

            if (!isFoundBlock) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy học kỳ block phù hợp"
                );
            }

            String userId = sessionHelper.getCurrentUserId();
            String folderName = "Exam/" + isSubjectExist.get().getSubjectCode() + "/" + request.getExamPaperType();
            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            postExamPaper.setPath(googleDriveFileDTO.getId());
            postExamPaper.setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
            postExamPaper.setExamPaperStatus(ExamPaperStatus.IN_USE);
            postExamPaper.setMajorFacility(isMajorFacilityExist.get());
            postExamPaper.setSubject(isSubjectExist.get());
            postExamPaper.setExamPaperCode(isSubjectExist.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3));
            postExamPaper.setExamPaperCreatedDate(new Date().getTime());
            postExamPaper.setStaffUpload(staffRepository.findById(userId).get());
            postExamPaper.setStatus(EntityStatus.ACTIVE);
            if (ExamPaperType.valueOf(request.getExamPaperType()).equals(ExamPaperType.MOCK_EXAM_PAPER)) {
                postExamPaper.setIsPublic(false);
            }
            examPaperRepository.save(postExamPaper);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Tải đề thi thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> updateExamPaper(@Valid UpdateExamPaperRequest request) {
        try {
            Optional<ExamPaper> isExamPaperExist = examPaperRepository.findById(request.getExamPaperId());
            if (isExamPaperExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Nội quy thi không được lớn hơn 5MB"
                );
            }

            Optional<MajorFacility> isMajorFacilityExist = majorFacilityRepository.findById(request.getMajorFacilityId());
            if (isMajorFacilityExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chuyên ngành cơ sở này"
                );
            }

            Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
            if (isSubjectExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            ExamPaper putExamPaper = isExamPaperExist.get();

            String oldFileId = putExamPaper.getPath();
            String folderName = "Exam/" + isSubjectExist.get().getSubjectCode() + "/" + request.getExamPaperType();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);
            putExamPaper.setPath(googleDriveFileDTO.getId());
            googleDriveFileService.deleteById(oldFileId);

            putExamPaper.setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
            if (ExamPaperType.valueOf(request.getExamPaperType()).equals(ExamPaperType.MOCK_EXAM_PAPER)) {
                putExamPaper.setIsPublic(false);
            }
            putExamPaper.setMajorFacility(isMajorFacilityExist.get());
            putExamPaper.setSubject(isSubjectExist.get());
            examPaperRepository.save(putExamPaper);


            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật đề thi thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> sendEmailPublicExamPaper(String examPaperId) {
        try {
            Optional<ExamPaper> optionalExamPaper = examPaperRepository.findById(examPaperId);
            if (optionalExamPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Không tìm thấy đề thi này"
                );
            }

            if (optionalExamPaper.get().getIsPublic()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Đề thi này đã được public"
                );
            }

            String blockId = sessionHelper.getCurrentBlockId();
            String subjectId = optionalExamPaper.get().getSubject().getId();
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
            String[] listEmailStaff = classSubjectRepository.getEmailStaffByBlockId(blockId, subjectId, departmentFacilityId);

            if (listEmailStaff.length == 0) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Môn học này chưa được xếp lớp học"
                );
            }
            emailService.sendEmailPublicMockExamPaper(listEmailStaff);

            ExamPaper examPaper = optionalExamPaper.get();
            examPaper.setIsPublic(true);
            examPaperRepository.save(examPaper);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Đề thi đã được public tới những giảng viên dạy môn " + examPaper.getSubject().getName()
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi xảy ra trong quá trình thực thi"
            );
        }
    }

}
