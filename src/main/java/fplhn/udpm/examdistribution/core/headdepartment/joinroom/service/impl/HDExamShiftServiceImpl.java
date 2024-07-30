package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDExamShiftJoinRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDFileResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDExamShiftService;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class HDExamShiftServiceImpl implements HDExamShiftService {

    private final HDExamShiftExtendRepository hdExamShiftExtendRepository;

    private final HDExamPaperExtendRepository hdExamPaperExtendRepository;

    private final HDClassSubjectExtendRepository hdClassSubjectExtendRepository;

    private final HDSStaffExtendRepository hdsStaffExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final SessionHelper sessionHelper;

    private final GoogleDriveFileService googleDriveFileService;

    private final EmailService emailService;

    @Override
    public boolean getExamShiftByRequest(String examShiftCode) {
        return hdExamShiftExtendRepository.findByExamShiftCode(examShiftCode).isPresent() &&
                hdExamShiftExtendRepository.getExamShiftByRequest(
                        examShiftCode,
                        sessionHelper.getCurrentUserDepartmentFacilityId(),
                        sessionHelper.getCurrentSemesterId()).isPresent();
    }

    @Override
    public ResponseObject<?> getAllExamShift() {
        try {
            Long currentDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime();
            String currentShiftString = Shift.getCurrentShift().toString();
            HDExamShiftRequest hdExamShiftRequest = new HDExamShiftRequest();
            hdExamShiftRequest.setDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
            hdExamShiftRequest.setSemesterId(sessionHelper.getCurrentSemesterId());
            hdExamShiftRequest.setCurrentDate(currentDateWithoutTime);
            hdExamShiftRequest.setCurrentShift(currentShiftString);
            return new ResponseObject<>(hdExamShiftExtendRepository
                    .getAllExamShift(hdExamShiftRequest),
                    HttpStatus.OK, "Lấy danh sách ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách ca thi!");
        }
    }

    @Override
    public ResponseObject<?> createExamShift(HDCreateExamShiftRequest hdCreateExamShiftRequest) {
        try {
            Long currentDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime();

            if (hdCreateExamShiftRequest.getExamDate() < currentDateWithoutTime) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ngày thi không hợp lệ!");
            }

            if (Objects.equals(hdCreateExamShiftRequest.getExamDate(), currentDateWithoutTime)
                    && Shift.valueOf(hdCreateExamShiftRequest.getShift())
                    .compareTo(Objects.requireNonNull(Shift.getCurrentShift())) < 0) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không hợp lệ!");
            }

            Optional<ClassSubject> existingClassSubject = hdClassSubjectExtendRepository
                    .findById(hdCreateExamShiftRequest.getClassSubjectId());
            if (existingClassSubject.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Lớp môn không tồn tại!");
            }

            if (hdExamShiftExtendRepository.countByClassSubjectId(hdCreateExamShiftRequest.getClassSubjectId()) == 3) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Lớp môn đã đủ 3 ca thi trong block của campus này!");
            }

            if (hdExamShiftExtendRepository.countByExamDateAndShiftAndRoom(hdCreateExamShiftRequest.getExamDate(),
                    hdCreateExamShiftRequest.getShift(), hdCreateExamShiftRequest.getRoom()) == 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi đã tồn tại!");
            }

            if (hdCreateExamShiftRequest.getFirstSupervisorCode()
                    .equals(hdCreateExamShiftRequest.getSecondSupervisorCode())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 1 và giám thị 2 không được trùng nhau!");
            }

//        Optional<Staff> existingFirstSupervisor = hdsStaffExtendRepository
//                .findByStaffCodeAndDepartmentFacilityId(hdCreateExamShiftRequest.getFirstSupervisorCode(),
//                        sessionHelper.getCurrentUserDepartmentFacilityId());
//        if (existingFirstSupervisor.isEmpty()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
//                    "Giám thị 1 không tồn tại hoặc không thuộc bộ môn này!");
//        }
//
//        Optional<Staff> existingSecondSupervisor = hdsStaffExtendRepository
//                .findByStaffCodeAndDepartmentFacilityId(hdCreateExamShiftRequest.getSecondSupervisorCode(),
//                        sessionHelper.getCurrentUserDepartmentFacilityId());
//        if (existingSecondSupervisor.isEmpty()) {
//            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
//                    "Giám thị 2 không tồn tại hoặc không thuộc bộ môn này!");
//        }

            Optional<Staff> existingFirstSupervisor = hdsStaffExtendRepository
                    .findByStaffCode(hdCreateExamShiftRequest.getFirstSupervisorCode());
            if (existingFirstSupervisor.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị 1 không tồn tại!");
            }

            Optional<Staff> existingSecondSupervisor = hdsStaffExtendRepository
                    .findByStaffCode(hdCreateExamShiftRequest.getSecondSupervisorCode());
            if (existingSecondSupervisor.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị 2 không tồn tại!");
            }

            if (hdExamShiftExtendRepository.countExistingFirstSupervisorByCurrentExamDateAndShift(
                    existingFirstSupervisor.get().getId(), hdCreateExamShiftRequest.getExamDate(),
                    hdCreateExamShiftRequest.getShift()) == 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 1 đang là giám thị ở ca thi khác!");
            }

            if (hdExamShiftExtendRepository.countExistingSecondSupervisorByCurrentExamDateAndShift(
                    existingSecondSupervisor.get().getId(), hdCreateExamShiftRequest.getExamDate(),
                    hdCreateExamShiftRequest.getShift()) == 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 2 đang là giám thị ở ca thi khác!");
            }


            String examShiftCode = CodeGenerator.generateRandomCode();
            String password = PasswordUtils.generatePassword();

            String salt = PasswordUtils.generateSalt();
            String securePassword = PasswordUtils.getSecurePassword(password, salt);

            ExamShift examShift = new ExamShift();
            examShift.setClassSubject(existingClassSubject.get());
            examShift.setExamShiftCode(examShiftCode);
            examShift.setFirstSupervisor(existingFirstSupervisor.get());
            examShift.setSecondSupervisor(existingSecondSupervisor.get());
            examShift.setExamDate(hdCreateExamShiftRequest.getExamDate());
            examShift.setShift(Shift.valueOf(hdCreateExamShiftRequest.getShift()));
            examShift.setRoom(hdCreateExamShiftRequest.getRoom());
            examShift.setHash(securePassword);
            examShift.setSalt(salt);
            examShift.setStatus(EntityStatus.ACTIVE);
            examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
            hdExamShiftExtendRepository.save(examShift);

            emailService.sendEmailWhenHeadDepartmentCreateExamShift(hdExamShiftExtendRepository.getContentSendMail(examShiftCode), password);

            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.CREATED, "Tạo ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tạo ca thi: ", e);
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST, "Lỗi khi tạo ca thi!");
        }
    }

    @Override
    public ResponseObject<?> joinExamShift(HDExamShiftJoinRequest joinRoomRequest) {
        try {
            Optional<ExamShift> existingExamShift = hdExamShiftExtendRepository
                    .findExamShiftByRequest(
                            joinRoomRequest.getExamShiftCodeJoin(),
                            sessionHelper.getCurrentUserDepartmentFacilityId(),
                            sessionHelper.getCurrentSemesterId());
            if (existingExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT,
                        "Ca thi không tồn tại!");
            }

            ExamShift examShift = existingExamShift.get();

            if (examShift.getExamShiftStatus().equals(ExamShiftStatus.FINISHED)) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT,
                        "Ca thi đã kết thúc!");
            }

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_HEAD_DEPARTMENT_JOIN_EXAM_SHIFT,
                    "Chủ nhiệm bộ môn đã tham gia ca thi " + examShift.getExamShiftCode());

            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.OK, "Tham gia ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tham gia ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi tham gia ca thi!");
        }
    }

    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hdExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ca thi: ", e);
            return new ResponseObject<>(hdExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin ca thi thành công!");
        }
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        try {
            return new ResponseObject<>(hdExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                    HttpStatus.OK, "Đếm số sinh viên trong ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi đếm số sinh viên trong ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi đếm số sinh viên trong ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        try {
            if (file.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi chưa có quy định thi hoặc có lỗi khi lấy file quy định thi!");
            }
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            return new ResponseObject<>(
                    new HDExamRuleResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "Lấy file quy định thi thành công!"
            );
        } catch (IOException e) {
            log.error("Lỗi khi lấy file quy định thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy file quy định thi!"
            );
        }
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hdExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy path đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy path đề thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy path đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException {
        try {
            if (file.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Lỗi khi lấy đề thi!");
            }
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            return new ResponseObject<>(
                    new HDFileResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK, "Lấy đề thi thành công!");
        } catch (IOException e) {
            log.error("Lỗi khi lấy đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy đề thi!");
        }
    }

}
