package fplhn.udpm.examdistribution.core.student.home.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.repository.ExamShiftStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.home.model.request.StudentExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.home.repository.StudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.home.service.StudentExamShiftService;
import fplhn.udpm.examdistribution.core.student.student.repository.StudentExtendRepository;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class StudentExamShiftServiceImpl implements StudentExamShiftService {

    private final ExamShiftStudentExtendRepository examShiftStudentExtendRepository;

    private final StudentExtendRepository studentExtendRepository;

    private final StudentExamShiftExtendRepository studentExamShiftExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean findStudentInExamShift(String examShiftCode) {
        Optional<ExamShift> existingExamShift = examShiftStudentExtendRepository
                .findByExamShiftCode(examShiftCode);
        if (existingExamShift.isEmpty()) {
            return false;
        }

        Optional<StudentExamShift> studentExamShift = studentExamShiftExtendRepository
                .findByExamShiftIdAndStudentId(existingExamShift.get().getId(), SessionConstant.CURRENT_USER_ID);
        if (studentExamShift.isEmpty() && SessionConstant.ROLE_LOGIN.equals("SINH_VIEN")) {
            return false;
        }

        return true;
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid StudentExamShiftRequest studentExamShiftRequest) {

        Optional<Student> existingStudent = studentExtendRepository.findById(studentExamShiftRequest.getStudentId());
        if (existingStudent.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                    "Sinh viên không tồn tại trong hệ thống!");
        }

        Optional<ExamShift> existingExamShift = examShiftStudentExtendRepository
                .findByExamShiftCode(studentExamShiftRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn hoặc mật khẩu không đúng!");
        }

        ExamShift examShift = existingExamShift.get();
        boolean passwordMatch = PasswordUtils.verifyUserPassword(studentExamShiftRequest.getPasswordJoin(),
                examShift.getHash(), examShift.getSalt());
        if (!passwordMatch) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn hoặc mật khẩu không đúng!");
        }

        StudentExamShift studentExamShift = new StudentExamShift();
        studentExamShift.setStudent(existingStudent.get());
        studentExamShift.setExamShift(examShift);
        studentExamShift.setJoinTime(studentExamShiftRequest.getJoinTime());
        studentExamShift.setExamStudentStatus(ExamStudentStatus.REGISTERED);
        studentExamShift.setStatus(EntityStatus.ACTIVE);
        studentExamShiftExtendRepository.save(studentExamShift);

        simpMessagingTemplate.convertAndSend("/topic/student-exam-shift",
                new NotificationResponse(
                        "Sinh viên "
                                + existingStudent.get().getStudentCode()
                                + " đã tham gia phòng thi!"));

        return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(examShiftStudentExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

}
