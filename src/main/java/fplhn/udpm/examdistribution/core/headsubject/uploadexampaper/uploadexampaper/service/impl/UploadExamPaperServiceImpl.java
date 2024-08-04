package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.impl;

import com.google.api.services.drive.model.Permission;
import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListStaffBySubjectIdRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.PublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.SharePermissionExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPSharePermissionExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPUploadExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.modal.request.SendEmailPublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
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

    private final UEPFacilityExtendRepository facilityRepository;

    private final UEPClassSubjectExtendRepository classSubjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final UEPSharePermissionExamPaperExtendRepository sharePermissionExamPaperRepository;

    private final RedisService redisService;

    private final EmailService emailService;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getListCurrentSubject() {
        try {
            String userId = sessionHelper.getCurrentUserId();

            String semesterId = sessionHelper.getCurrentSemesterId();
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
            return new ResponseObject<>(
                    subjectRepository.getListSubject(userId, departmentFacilityId, semesterId),
                    HttpStatus.OK,
                    "Lấy danh sách môn học thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy danh sách môn học không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> getListStaff() {
        try {
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
            return new ResponseObject<>(
                    staffRepository.getListStaff(departmentFacilityId),
                    HttpStatus.OK,
                    "Lấy danh sách nhân viên thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy danh sách nhân viên không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> getAllExamPaper(ListExamPaperRequest request) {
        try {
            Pageable pageable = Helper.createPageable(request, "createdDate");
            String userId = sessionHelper.getCurrentUserId();
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
            String semesterId = sessionHelper.getCurrentSemesterId();
            return new ResponseObject<>(
                    PageableObject.of(examPaperRepository.getListExamPaper(
                            pageable, request,
                            userId, departmentFacilityId,
                            semesterId, ExamPaperStatus.WAITING_APPROVAL.toString())
                    ),
                    HttpStatus.OK,
                    "Lấy danh sách đề thi thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy danh sách đề thi không thành công"
            );
        }
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
            log.error(e.getMessage());
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
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Thay đổi trạng thái đề thi không thành công"
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
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
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
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Tải đề thi không thành công"
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
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
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
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chỉnh sửa đề thi không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> sendEmailPublicMockExamPaper(String examPaperId) {
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

            ExamPaper examPaper = optionalExamPaper.get();
            examPaper.setIsPublic(true);
            examPaperRepository.save(examPaper);

            this.sendEmailPublicMockExamPaper(listEmailStaff, examPaper);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Đề thi đã được công khai tới những giảng viên dạy môn " + examPaper.getSubject().getName()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi xảy ra trong quá trình công khai đề thi thử"
            );
        }
    }

    @Override
    public ResponseObject<?> sendEmailPublicMockExamPaper(PublicMockExamPaperRequest request) {
        try {
            String blockId = sessionHelper.getCurrentBlockId();
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();

            Arrays.stream(request.getListExamPaperId()).forEach(examPaperId -> {
                Optional<ExamPaper> optionalExamPaper = examPaperRepository.findById(examPaperId);
                if (optionalExamPaper.isPresent()) {
                    String[] listEmailStaff = classSubjectRepository.getEmailStaffByBlockId(
                            blockId,
                            optionalExamPaper.get().getSubject().getId(),
                            departmentFacilityId
                    );
                    this.sendEmailPublicMockExamPaper(listEmailStaff, optionalExamPaper.get());

                    ExamPaper examPaper = optionalExamPaper.get();
                    examPaper.setIsPublic(true);
                    examPaperRepository.save(examPaper);
                }
            });

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Đề thi đã được công khai thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi xảy ra trong quá trình công khai đề thi thử"
            );
        }
    }

    private void sendEmailPublicMockExamPaper(String[] listBcc, ExamPaper examPaper) {
        SendEmailPublicMockExamPaperRequest sendEmailPublicMocExamPaper = new SendEmailPublicMockExamPaperRequest();
        sendEmailPublicMocExamPaper.setListEmailBcc(listBcc);
        sendEmailPublicMocExamPaper.setExamPaperCode(examPaper.getExamPaperCode());
        sendEmailPublicMocExamPaper.setTimeSend(new Date());
        sendEmailPublicMocExamPaper.setSubjectName(examPaper.getSubject().getName());
        sendEmailPublicMocExamPaper.setMajorName(examPaper.getMajorFacility().getMajor().getName());
        sendEmailPublicMocExamPaper.setDepartmentName(examPaper.getMajorFacility().getMajor().getDepartment().getName());
        sendEmailPublicMocExamPaper.setSemesterName(
                examPaper.getBlock().getSemester().getSemesterName()
                +
                " "
                +
                examPaper.getBlock().getSemester().getYear()
                +
                " - "
                +
                examPaper.getBlock().getName().name());
        emailService.sendEmailPublicMockExamPaper(sendEmailPublicMocExamPaper);

        Arrays.stream(listBcc).forEach(staff -> {
            googleDriveFileService.shareFile(
                    examPaper.getPath(),
                    staff
            );
        });
    }

    @Override
    public ResponseObject<?> getListStaffBySubjectId(String subjectId, ListStaffBySubjectIdRequest request) {
        try {
            String blockId = sessionHelper.getCurrentBlockId();
            String facilityId = sessionHelper.getCurrentUserFacilityId();
            Pageable pageable = Helper.createPageable(request, "createdDate");
            return new ResponseObject<>(
                    PageableObject.of(staffRepository.getListStaffBySubjectId(
                            pageable, subjectId, blockId, facilityId, request)
                    ),
                    HttpStatus.OK,
                    "Lấy danh sách môn học thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy danh sách môn học không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> sharePermissionExamPaper(SharePermissionExamPaperRequest request) {
        try {
            Optional<ExamPaper> examPaperOptional = examPaperRepository.findById(request.getExamPaperId());
            if (examPaperOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            }

            Optional<Block> blockOptional = blockRepository.findById(sessionHelper.getCurrentBlockId());
            if (blockOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy block hiện tại"
                );
            }

            Optional<Facility> facilityOptional = facilityRepository.findById(sessionHelper.getCurrentUserFacilityId());
            if (facilityOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cơ sở hiện tại"
                );
            }

            List<SharePermissionExamPaper> listPermissionExamPaperRequest = sharePermissionExamPaperRepository.getListSharePermissionExamPaperByExamPaperId(
                    examPaperOptional.get().getId(),
                    sessionHelper.getCurrentBlockId(),
                    sessionHelper.getCurrentUserFacilityId()
            );

            listPermissionExamPaperRequest.forEach(item -> {
                googleDriveFileService.deleteShareFile(
                        item.getExamPaper().getPath(),
                        item.getPermissionId()
                );
                sharePermissionExamPaperRepository.delete(item);
            });

            Arrays.stream(request.getListStaffId()).forEach(staffId -> {
                Optional<Staff> staffOptional = staffRepository.findById(staffId);

                if (staffOptional.isPresent()) {
                    SharePermissionExamPaper sharePermissionExamPaper = new SharePermissionExamPaper();
                    sharePermissionExamPaper.setBlock(blockOptional.get());
                    sharePermissionExamPaper.setFacility(facilityOptional.get());
                    sharePermissionExamPaper.setStaff(staffOptional.get());
                    sharePermissionExamPaper.setExamPaper(examPaperOptional.get());
                    sharePermissionExamPaper.setStatus(EntityStatus.ACTIVE);
                    Permission permission = googleDriveFileService.shareFile(
                            examPaperOptional.get().getPath(),
                            staffOptional.get().getAccountFpt()
                    );
                    sharePermissionExamPaper.setPermissionId(permission.getId());

                    sharePermissionExamPaperRepository.save(sharePermissionExamPaper);
                }
            });

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật quyền truy cập thành công"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chia sẻ quyền truy cập không thành công"
            );
        }
    }

}
