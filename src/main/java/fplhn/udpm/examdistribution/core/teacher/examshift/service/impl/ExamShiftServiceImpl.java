package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.repository.ClassSubjectTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.CreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.JoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.ExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.ExamShiftService;
import fplhn.udpm.examdistribution.core.teacher.staff.repository.StaffTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.student.repository.StudentTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.studentexamshift.repository.StudentExamShiftTeacherExtendRepository;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class ExamShiftServiceImpl implements ExamShiftService {

    private final ClassSubjectTeacherExtendRepository classSubjectTeacherExtendRepository;

    private final StaffTeacherExtendRepository staffTeacherExtendRepository;

    private final ExamShiftExtendRepository examShiftExtendRepository;

    private final StudentExamShiftTeacherExtendRepository studentExamShiftTeacherExtendRepository;

    private final StudentTeacherExtendRepository studentTeacherExtendRepository;

    private final HttpSession httpSession;

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Override
    public boolean findUsersInExamShift(String examShiftCode) {
        Optional<ExamShift> examShift = examShiftExtendRepository.findByExamShiftCode(examShiftCode);
        if (examShift.isEmpty()) {
            return false;
        }

        boolean isCurrentUserSupervisor
                = examShift.get().getFirstSupervisor().getId()
                          .equals(httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString())
                  || (examShift.get().getSecondSupervisor() != null
                      && examShift.get().getSecondSupervisor().getId()
                              .equals(httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString()));

        if (!isCurrentUserSupervisor && httpSession
                .getAttribute(SessionConstant.ROLE_LOGIN).toString().equals("GIANG_VIEN")) {
            return false;
        }

        return true;
    }

    @Override
    public ResponseObject<?> createExamShift(@Valid CreateExamShiftRequest createExamShiftRequest) {

        Optional<ClassSubject> existingClassSubject = classSubjectTeacherExtendRepository
                .findById(createExamShiftRequest.getClassSubjectId());
        if (existingClassSubject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Lớp môn không tồn tại!");
        }

        if (examShiftExtendRepository.countByClassSubjectId(createExamShiftRequest.getClassSubjectId()) == 3) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn đã đủ 3 ca thi trong block của campus này!");
        }

        if (examShiftExtendRepository.countByExamDateAndShiftAndRoom(createExamShiftRequest.getExamDate(),
                createExamShiftRequest.getShift(), createExamShiftRequest.getRoom()) == 1) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Phòng thi đã tồn tại!");
        }

        Optional<Staff> existingStaff = staffTeacherExtendRepository
                .findById(createExamShiftRequest.getFirstSupervisorId());
        if (existingStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị không tồn tại!");
        }

//        ResponseObject<?> validateShift = validateShift(createExamShiftRequest.getShift());
//        if (validateShift != null) {
//            return validateShift;
//        }

        ResponseObject<?> validatePassword = validatePassword(createExamShiftRequest.getPassword());
        if (validatePassword != null) {
            return validatePassword;
        }

        String examShiftCode = CodeGenerator.generateRandomCode();

        String salt = PasswordUtils.generateSalt();
        String password = PasswordUtils.getSecurePassword(createExamShiftRequest.getPassword(), salt);

        ExamShift examShift = new ExamShift();
        examShift.setClassSubject(existingClassSubject.get());
        examShift.setFirstSupervisor(existingStaff.get());
        examShift.setExamDate(createExamShiftRequest.getExamDate());
        examShift.setShift(Shift.valueOf(createExamShiftRequest.getShift()));
        examShift.setRoom(createExamShiftRequest.getRoom());
        examShift.setExamShiftCode(examShiftCode);
        examShift.setHash(password);
        examShift.setSalt(salt);
        examShift.setStatus(EntityStatus.ACTIVE);
        examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
        examShiftExtendRepository.save(examShift);

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.CREATED, "Tạo phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(examShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid JoinExamShiftRequest joinExamShiftRequest) {
        Optional<ExamShift> existingExamShift = findExamShiftByCode(joinExamShiftRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        ExamShift examShift = existingExamShift.get();
        boolean passwordMatch = PasswordUtils
                .verifyUserPassword(joinExamShiftRequest.getPasswordJoin(), examShift.getHash(), examShift.getSalt());
        if (!passwordMatch) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        Optional<Staff> existingStaff = staffTeacherExtendRepository
                .findById(joinExamShiftRequest.getSecondSupervisorId());
        if (existingStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị không tồn tại!");
        }

        if (examShift.getSecondSupervisor() != null) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Phòng thi đã đủ giám thị!");
        }

//        if (examShift.getFirstSupervisor().getId().equals(existingStaff.get().getId())) {
//            return new ResponseObject<>(null, HttpStatus.CONFLICT,
//                    "Giám thị " + existingStaff.get().getName() + " đang là giám thị 1 rồi!");
//        }

        examShift.setSecondSupervisor(existingStaff.get());
        examShiftExtendRepository.save(examShift);

        String accountFe = existingStaff.get().getAccountFe().split("@fe.edu.vn")[0];
        simpMessagingTemplate.convertAndSend("/topic/exam-shift",
                new NotificationResponse("Giám thị " + accountFe + " đã tham gia phòng thi!"));

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(examShiftExtendRepository.countStudentInExamShift(examShiftCode),
                HttpStatus.OK, "Đếm số sinh viên trong phòng thi thành công!");
    }

    @Override
    @Transactional
    public ResponseObject<?> removeStudent(String examShiftCode, String studentId, String reason) {
        Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
        if (examShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng thi không tồn tại!");
        }

        Optional<Student> student = findStudentById(studentId);
        if (student.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
        }

        Optional<StudentExamShift> studentExamShift
                = findStudentExamShift(examShift.get().getId(), student.get().getId());
        if (studentExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                    "Không có sinh viên trong phòng thi này!");
        }

        studentExamShift.get().setExamStudentStatus(ExamStudentStatus.KICKED);
        studentExamShift.get().setReason(reason);

        simpMessagingTemplate.convertAndSend("/topic/student-exam-shift-kick",
                new NotificationResponse("Sinh viên " + student.get().getName() + " đã bị kick ra khỏi phòng thi!"));

        return new ResponseObject<>(null, HttpStatus.OK, "Xoá sinh viên thành công!");
    }

    @Override
    public ResponseObject<?> approveStudent(String examShiftCode, String studentId) {
        Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
        if (examShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng thi không tồn tại!");
        }

        Optional<Student> student = findStudentById(studentId);
        if (student.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
        }

        Optional<StudentExamShift> studentExamShift
                = findStudentExamShift(examShift.get().getId(), student.get().getId());
        if (studentExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                    "Không có sinh viên trong phòng thi này!");
        }

        studentExamShift.get().setExamStudentStatus(ExamStudentStatus.REGISTERED);
        studentExamShiftTeacherExtendRepository.save(studentExamShift.get());

        simpMessagingTemplate.convertAndSend("/topic/student-exam-shift-approve",
                new NotificationResponse("Sinh viên " + student.get().getName() + " đã được phê duyệt vào phòng thi!"));

        return new ResponseObject<>(examShift.get().getExamShiftCode(), HttpStatus.OK,
                "Phê duyệt sinh viên " + student.get().getName() + " thành công!");
    }

    @Override
    public ResponseObject<?> refuseStudent(String examShiftCode, String studentId) {
        Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
        if (examShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng thi không tồn tại!");
        }

        Optional<Student> student = findStudentById(studentId);
        if (student.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
        }

        Optional<StudentExamShift> studentExamShift
                = findStudentExamShift(examShift.get().getId(), student.get().getId());
        if (studentExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                    "Không có sinh viên trong phòng thi này!");
        }

        studentExamShift.get().setExamStudentStatus(ExamStudentStatus.KICKED);
        studentExamShiftTeacherExtendRepository.save(studentExamShift.get());

        simpMessagingTemplate.convertAndSend("/topic/student-exam-shift-refuse",
                new NotificationResponse("Sinh viên " + student.get().getName() + " đã bị từ chối!"));

        return new ResponseObject<>(null, HttpStatus.OK,
                "Từ chối sinh viên " + student.get().getName() + " thành công!");
    }

    @Override
    public ResponseObject<?> startExamShift(String examShiftCode) {
        Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
        if (examShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng thi không tồn tại!");
        }

        examShift.get().setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);

        simpMessagingTemplate.convertAndSend("/topic/exam-shift-start",
                new NotificationResponse("Ca thi " + examShift.get().getExamShiftCode() + " đã bắt đầu!"));

        return new ResponseObject<>(examShift.get().getExamShiftCode(),
                HttpStatus.OK, "Bắt đầu ca thi thành công!");
    }

//    private ResponseObject<?> validateShift(String shift) {
//        Shift shiftEnum = Shift.valueOf(shift);
//        if (shiftEnum.ordinal() > Shift.CA6.ordinal()) {
//            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi không hợp lệ!");
//        }
//        return null;
//    }

    private ResponseObject<?> validatePassword(String password) {
        String regex = "^[a-zA-Z0-9]{1,15}$";
        if (!password.matches(regex)) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mật khẩu không hợp lệ!");
        }
        return null;
    }

    private Optional<ExamShift> findExamShiftByCode(String examShiftCode) {
        return examShiftExtendRepository.findByExamShiftCode(examShiftCode);
    }

    private Optional<Student> findStudentById(String studentId) {
        return studentTeacherExtendRepository.findById(studentId);
    }

    private Optional<StudentExamShift> findStudentExamShift(String examShiftId, String studentId) {
        return studentExamShiftTeacherExtendRepository.findByExamShiftIdAndStudentId(examShiftId, studentId);
    }

}
