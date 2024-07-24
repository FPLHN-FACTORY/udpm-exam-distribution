package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDCreateExamShiftRequest;
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
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
public class HDExamShiftServiceImpl implements HDExamShiftService {

    private final HDExamShiftExtendRepository hdExamShiftExtendRepository;

    private final HDExamPaperExtendRepository hdExamPaperExtendRepository;

    private final HDClassSubjectExtendRepository hdClassSubjectExtendRepository;

    private final HDSStaffExtendRepository hdsStaffExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final HttpSession httpSession;

    private final GoogleDriveFileService googleDriveFileService;

    @Override
    public boolean getExamShiftByRequest(String examShiftCode) {
        return hdExamShiftExtendRepository.findByExamShiftCode(examShiftCode).isPresent() &&
                hdExamShiftExtendRepository.getExamShiftByRequest(examShiftCode,
                        httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString()).isPresent();
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
        return new ResponseObject<>(hdExamShiftExtendRepository
                .getAllExamShift(httpSession.getAttribute(
                                SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString(),
                        currentTimeMillis, currentShiftString),
                HttpStatus.OK, "Lấy danh sách ca thi thành công!");
    }

    @Override
    public ResponseObject<?> createExamShift(HDCreateExamShiftRequest hdCreateExamShiftRequest) {
        Optional<ClassSubject> existingClassSubject = hdClassSubjectExtendRepository
                .findById(hdCreateExamShiftRequest.getClassSubjectId());
        if (existingClassSubject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn không tồn tại!");
        }

        if (hdExamShiftExtendRepository.countByClassSubjectId(hdCreateExamShiftRequest.getClassSubjectId()) == 3) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn đã đủ 3 ca thi trong block của campus này!");
        }

        if (hdExamShiftExtendRepository.countByExamDateAndShiftAndRoom(hdCreateExamShiftRequest.getExamDate(),
                hdCreateExamShiftRequest.getShift(), hdCreateExamShiftRequest.getRoom()) == 1) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Ca thi đã tồn tại!");
        }

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

//        ResponseObject<?> validateShift = validateShift(createExamShiftRequest.getShift());
//        if (validateShift != null) {
//            return validateShift;
//        }

        ResponseObject<?> validatePassword = validatePassword(hdCreateExamShiftRequest.getPassword());
        if (validatePassword != null) {
            return validatePassword;
        }

        String examShiftCode = CodeGenerator.generateRandomCode();

        String salt = PasswordUtils.generateSalt();
        String password = PasswordUtils.getSecurePassword(hdCreateExamShiftRequest.getPassword(), salt);

        ExamShift examShift = new ExamShift();
        examShift.setClassSubject(existingClassSubject.get());
        examShift.setExamShiftCode(examShiftCode);
        examShift.setFirstSupervisor(existingFirstSupervisor.get());
        examShift.setSecondSupervisor(existingSecondSupervisor.get());
        examShift.setExamDate(hdCreateExamShiftRequest.getExamDate());
        examShift.setShift(Shift.valueOf(hdCreateExamShiftRequest.getShift()));
        examShift.setRoom(hdCreateExamShiftRequest.getRoom());
        examShift.setHash(password);
        examShift.setSalt(salt);
        examShift.setStatus(EntityStatus.ACTIVE);
        examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
        hdExamShiftExtendRepository.save(examShift);

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
    public ResponseObject<?> joinExamShift(HDExamShiftRequest joinRoomRequest) {
        Optional<ExamShift> existingExamShift = hdExamShiftExtendRepository
                .findByExamShiftCode(joinRoomRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại!");
        }

        if (existingExamShift.get().getExamShiftStatus().equals(ExamShiftStatus.FINISHED)) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Ca thi đã kết thúc!");
        }

        ExamShift examShift = existingExamShift.get();

        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_HEAD_DEPARTMENT_JOIN_EXAM_SHIFT,
                "Chủ nhiệm bộ môn đã tham gia phòng thi " + examShift.getExamShiftCode());

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(hdExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(hdExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                HttpStatus.OK, "Đếm số sinh viên trong phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

        return new ResponseObject<>(
                new HDExamRuleResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK,
                "Lấy file quy định thi thành công!"
        );
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hdExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy path đề thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
        return new ResponseObject<>(
                new HDFileResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK, "Lấy đề thi thành công!");
    }

}
