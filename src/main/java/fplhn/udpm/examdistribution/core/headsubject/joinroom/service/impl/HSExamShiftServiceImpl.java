package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSCreateExamShiftRequest;
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
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Base64;
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

    @Override
    public boolean getExamShiftByRequest(String examShiftCode) {
        return hsExamShiftExtendRepository.findByExamShiftCode(examShiftCode).isPresent() &&
                hsExamShiftExtendRepository.getExamShiftByRequest(examShiftCode,
                        sessionHelper.getCurrentUserDepartmentFacilityId()).isPresent();
    }

    @Override
    public ResponseObject<?> getAllExamShift() {
        LocalDate currentDate = LocalDate.now();
        LocalDateTime startOfDay = currentDate.atStartOfDay();
        ZonedDateTime zonedDateTime = startOfDay.atZone(ZoneId.systemDefault());
        Instant instant = zonedDateTime.toInstant();
        Long currentTimeMillis = instant.toEpochMilli();

        Shift currentShift = Shift.getCurrentShift();
        String currentShiftString = currentShift.toString();
        return new ResponseObject<>(hsExamShiftExtendRepository
                .getAllExamShift(sessionHelper.getCurrentUserDepartmentFacilityId(),
                        sessionHelper.getCurrentUserId(),
                        currentTimeMillis, currentShiftString),
                HttpStatus.OK, "Lấy danh sách ca thi thành công!");
    }

    @Override
    public ResponseObject<?> createExamShift(HSCreateExamShiftRequest hsCreateExamShiftRequest) {
        Optional<ClassSubject> existingClassSubject = hsClassSubjectExtendRepository
                .findById(hsCreateExamShiftRequest.getClassSubjectId());
        if (existingClassSubject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn không tồn tại!");
        }

        if (hsExamShiftExtendRepository.countByClassSubjectId(hsCreateExamShiftRequest.getClassSubjectId()) == 3) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn đã đủ 3 ca thi trong block của campus này!");
        }

        if (hsExamShiftExtendRepository.countByExamDateAndShiftAndRoom(hsCreateExamShiftRequest.getExamDate(),
                hsCreateExamShiftRequest.getShift(), hsCreateExamShiftRequest.getRoom()) == 1) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Ca thi đã tồn tại!");
        }

        Optional<Staff> existingFirstSupervisor = hsStaffExtendRepository
                .findByStaffCode(hsCreateExamShiftRequest.getFirstSupervisorCode());
        if (existingFirstSupervisor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị 1 không tồn tại!");
        }

        Optional<Staff> existingSecondSupervisor = hsStaffExtendRepository
                .findByStaffCode(hsCreateExamShiftRequest.getSecondSupervisorCode());
        if (existingSecondSupervisor.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị 2 không tồn tại!");
        }

//        ResponseObject<?> validateShift = validateShift(createExamShiftRequest.getShift());
//        if (validateShift != null) {
//            return validateShift;
//        }

        ResponseObject<?> validatePassword = validatePassword(hsCreateExamShiftRequest.getPassword());
        if (validatePassword != null) {
            return validatePassword;
        }

        String examShiftCode = CodeGenerator.generateRandomCode();

        String salt = PasswordUtils.generateSalt();
        String password = PasswordUtils.getSecurePassword(hsCreateExamShiftRequest.getPassword(), salt);

        ExamShift examShift = new ExamShift();
        examShift.setClassSubject(existingClassSubject.get());
        examShift.setExamShiftCode(examShiftCode);
        examShift.setFirstSupervisor(existingFirstSupervisor.get());
        examShift.setSecondSupervisor(existingSecondSupervisor.get());
        examShift.setExamDate(hsCreateExamShiftRequest.getExamDate());
        examShift.setShift(Shift.valueOf(hsCreateExamShiftRequest.getShift()));
        examShift.setRoom(hsCreateExamShiftRequest.getRoom());
        examShift.setHash(password);
        examShift.setSalt(salt);
        examShift.setStatus(EntityStatus.ACTIVE);
        examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
        hsExamShiftExtendRepository.save(examShift);

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.CREATED, "Tạo ca thi thành công!");
    }

    private ResponseObject<?> validatePassword(String password) {
        String regex = "^[a-zA-Z0-9]{1,15}$";
        if (!password.matches(regex)) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mật khẩu không hợp lệ!");
        }
        return null;
    }

    @Override
    public ResponseObject<?> joinExamShift(HSExamShiftServiceRequest joinRoomRequest) {
        Optional<ExamShift> existingExamShift = hsExamShiftExtendRepository
                .findByExamShiftCode(joinRoomRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại!");
        }

        ExamShift examShift = existingExamShift.get();

        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_HEAD_SUBJECT_JOIN_EXAM_SHIFT,
                "Trưởng môn đã tham gia phòng thi " + examShift.getExamShiftCode());

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(hsExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(hsExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                HttpStatus.OK, "Đếm số sinh viên trong phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

        return new ResponseObject<>(
                new HSExamRuleResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK,
                "Lấy file quy định thi thành công!"
        );
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hsExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy path đề thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
        return new ResponseObject<>(
                new HSFileResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK, "Lấy đề thi thành công!");
    }

}
