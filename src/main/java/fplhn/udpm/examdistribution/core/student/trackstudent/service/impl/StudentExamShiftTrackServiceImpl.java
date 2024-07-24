package fplhn.udpm.examdistribution.core.student.trackstudent.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.CheckRoomIsValidRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.SaveTrackUrlRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.repository.SESTExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.trackstudent.repository.SESTStudentExamShiftTrackExtendRepository;
import fplhn.udpm.examdistribution.core.student.trackstudent.repository.SESTStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.trackstudent.service.StudentExamShiftTrackService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShiftTrack;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityProperties;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentExamShiftTrackServiceImpl implements StudentExamShiftTrackService {

    private final SESTStudentExamShiftTrackExtendRepository examShiftTrackExtendRepository;

    private final SESTStudentExtendRepository studentRepository;

    private final SESTExamShiftExtendRepository examShiftRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> checkExamShiftIsValid(CheckRoomIsValidRequest request) {
        String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
        Long currentTime = new Date().getTime() - 900000;
        Object examShiftInfo = examShiftTrackExtendRepository.getExamShiftInfo(request, blockId);

        if (examShiftInfo == null) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi không hợp lệ");
        } else {
            return new ResponseObject<>(
                    examShiftInfo,
                    HttpStatus.OK,
                    "Exam Shift hợp lệ"
            );
        }
    }

    @Override
    public ResponseObject<?> saveTrackUrl(SaveTrackUrlRequest request) {
        if (request.getUrl().length() < EntityProperties.LENGTH_URL) {


            Optional<Student> studentOptional = studentRepository.getStudentsByEmail(request.getEmail());
            Optional<ExamShift> examShiftOptional = examShiftRepository.getExamShiftByExamShiftCode(request.getRoomCode());

            if (studentOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy sinh viên này"
                );
            }

            if (examShiftOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy ca thi này"
                );
            }

            StudentExamShiftTrack studentExamShiftTrack = new StudentExamShiftTrack();
            studentExamShiftTrack.setStudent(studentOptional.get());
            studentExamShiftTrack.setExamShift(examShiftOptional.get());
            studentExamShiftTrack.setTimeViolation(new Date().getTime());
            studentExamShiftTrack.setUrl(request.getUrl());
            studentExamShiftTrack.setStatus(EntityStatus.ACTIVE);

            examShiftTrackExtendRepository.save(studentExamShiftTrack);

            String messageTrack = "";
            if (request.getIsDisable()) {
                messageTrack = "Sinh viên " + studentOptional.get().getEmail() + " đã tắt extension";
            } else {
                messageTrack = "Sinh viên " + studentOptional.get().getEmail() + " đã bật 1 tab khác";
            }
            simpMessagingTemplate.convertAndSend("/topic/track-student",
                    new NotificationResponse(messageTrack));

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Lưu dữ liệu thành công"
            );
        } else {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Quá độ dài url quá dài"
            );
        }
    }

}
