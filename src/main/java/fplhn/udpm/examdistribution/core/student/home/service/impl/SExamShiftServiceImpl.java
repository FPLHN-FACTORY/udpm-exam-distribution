package fplhn.udpm.examdistribution.core.student.home.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.home.model.request.SExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.exampaper.model.request.SOpenExamPaperRequest;
import fplhn.udpm.examdistribution.core.student.exampaper.repository.SExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.student.exampapershift.repository.SExamPaperShiftRepository;
import fplhn.udpm.examdistribution.core.student.studentexamshift.repository.SStudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.home.service.SExamShiftService;
import fplhn.udpm.examdistribution.core.student.student.repository.SStudentExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TFileResourceResponse;
import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class SExamShiftServiceImpl implements SExamShiftService {

    private final SExamShiftExtendRepository sExamShiftExtendRepository;

    private final SStudentExtendRepository studentExtendRepository;

    private final SStudentExamShiftExtendRepository sStudentExamShiftExtendRepository;

    private final SExamPaperExtendRepository sExamPaperExtendRepository;

    private final SExamPaperShiftRepository sExamPaperShiftRepository;

    private final HttpSession httpSession;

    private final GoogleDriveFileService googleDriveFileService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean findStudentInExamShift(String examShiftCode) {
        Optional<ExamShift> existingExamShift = sExamShiftExtendRepository
                .findByExamShiftCode(examShiftCode);
        if (existingExamShift.isEmpty()) {
            return false;
        }

        Optional<StudentExamShift> studentExamShift = sStudentExamShiftExtendRepository
                .findByExamShiftIdAndStudentId(existingExamShift.get().getId(),
                        httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString());

        if (studentExamShift.isEmpty()
            || studentExamShift.get().getExamStudentStatus().toString().matches("DONE_EXAM|KICKED|REJOINED")
               && httpSession.getAttribute(SessionConstant.ROLE_LOGIN).toString().equals("SINH_VIEN")) {
            return false;
        }

        return true;
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid SExamShiftRequest sExamShiftRequest) {

        Optional<Student> existingStudent = studentExtendRepository.findById(sExamShiftRequest.getStudentId());
        if (existingStudent.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                    "Sinh viên không tồn tại trong hệ thống!");
        }

        Optional<ExamShift> existingExamShift = sExamShiftExtendRepository
                .findByExamShiftCode(sExamShiftRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        ExamShift examShift = existingExamShift.get();
        boolean passwordMatch = PasswordUtils.verifyUserPassword(sExamShiftRequest.getPasswordJoin(),
                examShift.getHash(), examShift.getSalt());
        if (!passwordMatch) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        Optional<StudentExamShift> studentExamShiftExist = sStudentExamShiftExtendRepository
                .findByExamShiftIdAndStudentId(examShift.getId(), sExamShiftRequest.getStudentId());
        if (studentExamShiftExist.isPresent()) {
            ExamStudentStatus examStudentStatus = studentExamShiftExist.get().getExamStudentStatus();
            if (examStudentStatus.equals(ExamStudentStatus.KICKED)
                || examStudentStatus.equals(ExamStudentStatus.REJOINED)) {
                studentExamShiftExist.get().setExamStudentStatus(ExamStudentStatus.REJOINED);
                sStudentExamShiftExtendRepository.save(studentExamShiftExist.get());
                simpMessagingTemplate.convertAndSend("/topic/student-exam-shift-rejoin",
                        new NotificationResponse(
                                "Sinh viên "
                                + existingStudent.get().getStudentCode()
                                + " yêu cầu tham gia phòng thi!"));
                return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                        HttpStatus.OK, "Vui lòng chờ giám thị phê duyệt!");
            } else {
                StudentExamShift studentExamShift = studentExamShiftExist.get();
                studentExamShift.setId(studentExamShiftExist.get().getId());
                studentExamShift.setStudent(existingStudent.get());
                studentExamShift.setExamShift(examShift);
                studentExamShift.setJoinTime(sExamShiftRequest.getJoinTime());
                studentExamShift.setExamStudentStatus(ExamStudentStatus.REGISTERED);
                studentExamShift.setStatus(EntityStatus.ACTIVE);
                sStudentExamShiftExtendRepository.save(studentExamShift);
                return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                        HttpStatus.OK, "Tham gia phòng thi thành công!");
            }

        } else {
            StudentExamShift studentExamShift = new StudentExamShift();
            studentExamShift.setStudent(existingStudent.get());
            studentExamShift.setExamShift(examShift);
            studentExamShift.setJoinTime(sExamShiftRequest.getJoinTime());
            studentExamShift.setExamStudentStatus(ExamStudentStatus.REGISTERED);
            studentExamShift.setStatus(EntityStatus.ACTIVE);
            sStudentExamShiftExtendRepository.save(studentExamShift);

            simpMessagingTemplate.convertAndSend("/topic/student-exam-shift",
                    new NotificationResponse(
                            "Sinh viên "
                            + existingStudent.get().getStudentCode()
                            + " đã tham gia phòng thi!"));
        }

        return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(sExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(sExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy path đề thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
        return new ResponseObject<>(
                new TFileResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK, "Lấy đề thi thành công!");
    }

    @Override
    public ResponseObject<?> openExamPaper(SOpenExamPaperRequest sOpenExamPaperRequest) {
        ExamPaperShift examPaperShift
                = sExamPaperShiftRepository.getReferenceById(sOpenExamPaperRequest.getExamPaperShiftId());

        boolean passwordMatch = PasswordUtils.verifyUserPassword(sOpenExamPaperRequest.getPasswordOpen(),
                examPaperShift.getHash(), examPaperShift.getSalt());
        if (!passwordMatch) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Mật khẩu không đúng!");
        }

        return new ResponseObject<>(null, HttpStatus.OK, "Mở đề thi thành công!");
    }

    @Override
    public ResponseObject<?> updateExamStudentStatus(String examShiftCode) {
        return null;
    }

}
