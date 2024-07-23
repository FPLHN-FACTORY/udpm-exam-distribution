package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDFileResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.repository.HDExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.repository.HDExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.HDExamShiftService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
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
