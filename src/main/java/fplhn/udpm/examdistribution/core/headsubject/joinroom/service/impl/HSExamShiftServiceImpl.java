package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSExamShiftRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSExamShiftServiceRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSFileResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSExamShiftService;
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
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class HSExamShiftServiceImpl implements HSExamShiftService {

    private final HSExamShiftExtendRepository hsExamShiftExtendRepository;

    private final HSExamPaperExtendRepository hsExamPaperExtendRepository;

    private final HSClassSubjectExtendRepository hsClassSubjectExtendRepository;

    private final HSStaffExtendRepository hsStaffExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final SessionHelper sessionHelper;

    private final GoogleDriveFileService googleDriveFileService;

    private final EmailService emailService;

    @Override
    public boolean getExamShiftByRequest(String examShiftCode) {
        return hsExamShiftExtendRepository.findByExamShiftCode(examShiftCode).isPresent() &&
                hsExamShiftExtendRepository.getExamShiftByRequest(
                        examShiftCode,
                        sessionHelper.getCurrentUserDepartmentFacilityId(),
                        sessionHelper.getCurrentSemesterId()).isPresent();
    }

    @Override
    public ResponseObject<?> getAllExamShift() {
        try {
            Long currentDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime();
            String currentShiftString = Shift.getCurrentShift().toString();
            HSExamShiftRequest hsExamShiftRequest = new HSExamShiftRequest();
            hsExamShiftRequest.setDepartmentFacilityId(sessionHelper.getCurrentUserDepartmentFacilityId());
            hsExamShiftRequest.setSemesterId(sessionHelper.getCurrentSemesterId());
            hsExamShiftRequest.setStaffId(sessionHelper.getCurrentUserId());
            hsExamShiftRequest.setCurrentDate(currentDateWithoutTime);
            hsExamShiftRequest.setCurrentShift(currentShiftString);
            return new ResponseObject<>(hsExamShiftExtendRepository
                    .getAllExamShift(hsExamShiftRequest), HttpStatus.OK, "Lấy danh sách ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy danh sách ca thi!");
        }
    }

    @Override
    public ResponseObject<?> createExamShift(HSCreateExamShiftRequest hsCreateExamShiftRequest) {
        try {
            Long currentDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime();

            if (hsCreateExamShiftRequest.getExamDate() < currentDateWithoutTime) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ngày thi không hợp lệ!");
            }

            if (Objects.equals(hsCreateExamShiftRequest.getExamDate(), currentDateWithoutTime)
                    && Shift.valueOf(hsCreateExamShiftRequest.getShift())
                    .compareTo(Objects.requireNonNull(Shift.getCurrentShift())) < 0) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không hợp lệ!");
            }

//            if (validateShift(hsCreateExamShiftRequest.getShift()) != null) {
//                return validateShift(hsCreateExamShiftRequest.getShift());
//            }

            Optional<ClassSubject> existingClassSubject = hsClassSubjectExtendRepository
                    .findById(hsCreateExamShiftRequest.getClassSubjectId());
            if (existingClassSubject.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Lớp môn không tồn tại!");
            }

            if (hsExamShiftExtendRepository.countByClassSubjectId(hsCreateExamShiftRequest.getClassSubjectId()) == 3) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Lớp môn đã đủ 3 ca thi trong block của campus này!");
            }

            if (hsExamShiftExtendRepository.countByExamDateAndShiftAndRoom(hsCreateExamShiftRequest.getExamDate(),
                    hsCreateExamShiftRequest.getShift(), hsCreateExamShiftRequest.getRoom()) >= 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi đã tồn tại!");
            }

            if (hsExamShiftExtendRepository.countByExamDateAndShiftAndClassSubjectId(
                    hsCreateExamShiftRequest.getExamDate(), hsCreateExamShiftRequest.getShift(),
                    hsCreateExamShiftRequest.getClassSubjectId()) >= 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Lớp môn này đang có 1 ca thi khác ở thời điểm hiện tại!");
            }

            if (hsCreateExamShiftRequest.getFirstSupervisorCode()
                    .equals(hsCreateExamShiftRequest.getSecondSupervisorCode())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 1 và giám thị 2 không được trùng nhau!");
            }

            Optional<Staff> existingFirstSupervisor = hsStaffExtendRepository
                    .findByStaffCodeAndDepartmentFacilityId(hsCreateExamShiftRequest.getFirstSupervisorCode(),
                            sessionHelper.getCurrentUserDepartmentFacilityId());
            if (existingFirstSupervisor.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Giám thị 1 không tồn tại hoặc không thuộc bộ môn này!");
            }

            Optional<Staff> existingSecondSupervisor = hsStaffExtendRepository
                    .findByStaffCodeAndDepartmentFacilityId(hsCreateExamShiftRequest.getSecondSupervisorCode(),
                            sessionHelper.getCurrentUserDepartmentFacilityId());
            if (existingSecondSupervisor.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Giám thị 2 không tồn tại hoặc không thuộc bộ môn này!");
            }

//            Optional<Staff> existingFirstSupervisor = hsStaffExtendRepository
//                    .findByStaffCode(hsCreateExamShiftRequest.getFirstSupervisorCode());
//            if (existingFirstSupervisor.isEmpty()) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
//                        "Giám thị 1 không tồn tại!");
//            }
//
//            Optional<Staff> existingSecondSupervisor = hsStaffExtendRepository
//                    .findByStaffCode(hsCreateExamShiftRequest.getSecondSupervisorCode());
//            if (existingSecondSupervisor.isEmpty()) {
//                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
//                        "Giám thị 2 không tồn tại!");
//            }

            if (hsExamShiftExtendRepository.countExistingFirstSupervisorByCurrentExamDateAndShift(
                    existingFirstSupervisor.get().getId(), hsCreateExamShiftRequest.getExamDate(),
                    hsCreateExamShiftRequest.getShift()) == 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 1 đang là giám thị ở ca thi khác!");
            }

            if (hsExamShiftExtendRepository.countExistingSecondSupervisorByCurrentExamDateAndShift(
                    existingSecondSupervisor.get().getId(), hsCreateExamShiftRequest.getExamDate(),
                    hsCreateExamShiftRequest.getShift()) == 1) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Giám thị 2 đang là giám thị ở ca thi khác!");
            }

            String examShiftCode;
            List<String> listExamShiftCode = hsExamShiftExtendRepository.getAllExamShiftCode();
            do {
                examShiftCode = CodeGenerator.generateRandomCode();
            } while (listExamShiftCode.contains(examShiftCode));

            String password = PasswordUtils.generatePassword();

            ExamShift examShift = new ExamShift();
            examShift.setClassSubject(existingClassSubject.get());
            examShift.setExamShiftCode(examShiftCode);
            examShift.setFirstSupervisor(existingFirstSupervisor.get());
            examShift.setSecondSupervisor(existingSecondSupervisor.get());
            examShift.setExamDate(hsCreateExamShiftRequest.getExamDate());
            examShift.setShift(Shift.valueOf(hsCreateExamShiftRequest.getShift()));
            examShift.setRoom(hsCreateExamShiftRequest.getRoom());
            examShift.setPassword(password);
            examShift.setStatus(EntityStatus.ACTIVE);
            examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
            hsExamShiftExtendRepository.save(examShift);

            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.CREATED, "Tạo ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tạo ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi tạo ca thi!");
        }
    }

    private ResponseObject<?> validateShift(String shift) {
        Shift shiftEnum = Shift.valueOf(shift);
        if (shiftEnum.ordinal() > Shift.CA6.ordinal()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi không hợp lệ!");
        }
        return null;
    }

    @Override
    public ResponseObject<?> joinExamShift(HSExamShiftServiceRequest joinRoomRequest) {
        try {
            Optional<ExamShift> existingExamShift = hsExamShiftExtendRepository
                    .findExamShiftByRequest(
                            joinRoomRequest.getExamShiftCodeJoin(),
                            sessionHelper.getCurrentUserDepartmentFacilityId(),
                            sessionHelper.getCurrentSemesterId());
            if (existingExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Ca thi không tồn tại!");
            }

            ExamShift examShift = existingExamShift.get();

            if (examShift.getExamShiftStatus().equals(ExamShiftStatus.FINISHED)) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi đã kết thúc!");
            }

//            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_HEAD_SUBJECT_JOIN_EXAM_SHIFT,
//                    "Trưởng môn đã tham gia ca thi " + examShift.getExamShiftCode());

            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.OK, "Tham gia ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tham gia ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi tham gia ca thi!");
        }
    }

    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hsExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy thông tin ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getStartTimeEndTimeExamPaperByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hsExamPaperExtendRepository.getStartTimeEndTimeExamPaperByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thời gian bắt đầu và kết thúc đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thời gian bắt đầu và kết thúc đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thời gian bắt đầu và kết thúc đề thi!");
        }
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        try {
            return new ResponseObject<>(hsExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                    HttpStatus.OK, "Đếm số sinh viên trong ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi đếm số sinh viên trong ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi đếm số sinh viên trong ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) {
        try {
            if (file.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi chưa có quy định thi / có lỗi khi lấy file quy định thi!");
            }
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            return new ResponseObject<>(
                    new HSExamRuleResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK, "Lấy file quy định thi thành công!");
        } catch (IOException e) {
            log.error("Lỗi khi lấy file quy định thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy file quy định thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(
                    hsExamPaperExtendRepository
                            .getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy path đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy path đề thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy path đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) {
        try {
            if (file.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Lỗi khi lấy đề thi!");
            }
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            return new ResponseObject<>(
                    new HSFileResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK, "Lấy đề thi thành công!");
        } catch (IOException e) {
            log.error("Lỗi khi lấy đề thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy đề thi!");
        }
    }

}
