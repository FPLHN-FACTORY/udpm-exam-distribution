package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.JoinRoomRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.repository.HSExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.JoinRoomService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class JoinRoomServiceImpl implements JoinRoomService {

    private final HSExamShiftExtendRepository hsExamShiftExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public ResponseObject<?> joinExamShift(JoinRoomRequest joinRoomRequest) {
        Optional<ExamShift> existingExamShift = hsExamShiftExtendRepository
                .findByExamShiftCode(joinRoomRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        ExamShift examShift = existingExamShift.get();
        boolean passwordMatch = PasswordUtils
                .verifyUserPassword(joinRoomRequest.getPasswordJoin(), examShift.getHash(), examShift.getSalt());
        if (!passwordMatch) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        simpMessagingTemplate.convertAndSend("/topic/head-subject-exam-shift-join",
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

}
