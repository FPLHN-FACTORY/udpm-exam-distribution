package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.repository.HDExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.HDExamShiftService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class HDExamShiftServiceImpl implements HDExamShiftService {

    private static final Logger log = LoggerFactory.getLogger(HDExamShiftServiceImpl.class);
    private final HDExamShiftExtendRepository hdExamShiftExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final HttpSession httpSession;

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

        simpMessagingTemplate.convertAndSend("/topic/head-department-exam-shift-join",
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
    public ResponseObject<?> getPathByExamShiftCode(String examShiftCode) {
        return null;
    }

}
