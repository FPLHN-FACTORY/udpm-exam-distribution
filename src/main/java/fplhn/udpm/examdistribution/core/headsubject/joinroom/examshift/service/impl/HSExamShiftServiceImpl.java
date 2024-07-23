package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.HSExamShiftServiceRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response.HSExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response.HSFileResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.repository.HSExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.repository.HSExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.HSExamShiftService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
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
