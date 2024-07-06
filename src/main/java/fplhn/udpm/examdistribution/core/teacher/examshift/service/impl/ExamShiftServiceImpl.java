package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.repository.ClassSubjectTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.CreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.JoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.ExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.ExamShiftService;
import fplhn.udpm.examdistribution.core.teacher.staff.repository.StaffTeacherExtendRepository;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.conflig.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
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

    private final SimpMessagingTemplate simpMessagingTemplate;

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

        ResponseObject<?> validateShift = validateShift(createExamShiftRequest.getShift());
        if (validateShift != null) {
            return validateShift;
        }

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
        Optional<ExamShift> existingExamShift = examShiftExtendRepository
                .findByExamShiftCode(joinExamShiftRequest.getExamShiftCodeJoin());
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
                new NotificationResponse( "Giám thị " + accountFe + " đã tham gia phòng thi!"));

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(examShiftExtendRepository.countStudentInExamShift(examShiftCode),
                HttpStatus.OK, "Đếm số sinh viên trong phòng thi thành công!");
    }

    private ResponseObject<?> validateShift(String shift) {
        Shift shiftEnum = Shift.valueOf(shift);
        if (shiftEnum.ordinal() > Shift.CA6.ordinal()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi không hợp lệ!");
        }
        return null;
    }

    private ResponseObject<?> validatePassword(String password) {
        String regex = "^[a-zA-Z0-9]{1,15}$";
        if (!password.matches(regex)) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mật khẩu không hợp lệ!");
        }
        return null;
    }

}
