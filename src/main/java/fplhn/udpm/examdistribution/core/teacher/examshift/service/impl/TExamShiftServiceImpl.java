package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.repository.TClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.exampaper.repository.TExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.exampaperbysemester.repository.TExamPaperBySemesterExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.exampapershift.model.response.TExamPaperShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.exampapershift.repository.TExamPaperShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TJoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TFileResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TStartExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TExamShiftService;
import fplhn.udpm.examdistribution.core.teacher.staff.repository.TStaffExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.student.repository.TStudentExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.studentexamshift.repository.TStudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
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
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class TExamShiftServiceImpl implements TExamShiftService {

    private final TClassSubjectExtendRepository tClassSubjectExtendRepository;

    private final TStaffExtendRepository tStaffExtendRepository;

    private final TExamShiftExtendRepository tExamShiftExtendRepository;

    private final TStudentExamShiftExtendRepository tStudentExamShiftExtendRepository;

    private final TStudentExtendRepository tStudentExtendRepository;

    private final TExamPaperExtendRepository tExamPaperExtendRepository;

    private final TExamPaperShiftExtendRepository tExamPaperShiftExtendRepository;

    private final TExamPaperBySemesterExtendRepository tExamPaperBySemesterExtendRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final HttpSession httpSession;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final EmailService emailService;

    @Override
    public boolean findUsersInExamShift(String examShiftCode) {
        Optional<ExamShift> examShift = tExamShiftExtendRepository.findByExamShiftCode(examShiftCode);
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
    public ResponseObject<?> createExamShift(@Valid TCreateExamShiftRequest tCreateExamShiftRequest) {

        Optional<ClassSubject> existingClassSubject = tClassSubjectExtendRepository
                .findById(tCreateExamShiftRequest.getClassSubjectId());
        if (existingClassSubject.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Lớp môn không tồn tại!");
        }

        if (tExamShiftExtendRepository.countByClassSubjectId(tCreateExamShiftRequest.getClassSubjectId()) == 3) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Lớp môn đã đủ 3 ca thi trong block của campus này!");
        }

        if (tExamShiftExtendRepository.countByExamDateAndShiftAndRoom(tCreateExamShiftRequest.getExamDate(),
                tCreateExamShiftRequest.getShift(), tCreateExamShiftRequest.getRoom()) == 1) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Phòng thi đã tồn tại!");
        }

        Optional<Staff> existingStaff = tStaffExtendRepository
                .findById(tCreateExamShiftRequest.getFirstSupervisorId());
        if (existingStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị không tồn tại!");
        }

//        ResponseObject<?> validateShift = validateShift(createExamShiftRequest.getShift());
//        if (validateShift != null) {
//            return validateShift;
//        }

        ResponseObject<?> validatePassword = validatePassword(tCreateExamShiftRequest.getPassword());
        if (validatePassword != null) {
            return validatePassword;
        }

        String examShiftCode = CodeGenerator.generateRandomCode();

        String salt = PasswordUtils.generateSalt();
        String password = PasswordUtils.getSecurePassword(tCreateExamShiftRequest.getPassword(), salt);

        ExamShift examShift = new ExamShift();
        examShift.setClassSubject(existingClassSubject.get());
        examShift.setFirstSupervisor(existingStaff.get());
        examShift.setExamDate(tCreateExamShiftRequest.getExamDate());
        examShift.setShift(Shift.valueOf(tCreateExamShiftRequest.getShift()));
        examShift.setRoom(tCreateExamShiftRequest.getRoom());
        examShift.setExamShiftCode(examShiftCode);
        examShift.setHash(password);
        examShift.setSalt(salt);
        examShift.setStatus(EntityStatus.ACTIVE);
        examShift.setExamShiftStatus(ExamShiftStatus.NOT_STARTED);
        tExamShiftExtendRepository.save(examShift);

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.CREATED, "Tạo phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(tExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid TJoinExamShiftRequest tJoinExamShiftRequest) {
        Optional<ExamShift> existingExamShift = findExamShiftByCode(tJoinExamShiftRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }
        ExamShift examShift = existingExamShift.get();

        if (!PasswordUtils.verifyUserPassword(tJoinExamShiftRequest
                .getPasswordJoin(), examShift.getHash(), examShift.getSalt())) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại hoặc mật khẩu không đúng!");
        }

        if (examShift.getExamShiftStatus() == ExamShiftStatus.FINISHED) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT, "Ca thi đã kết thúc!");
        }

        Optional<Staff> existingStaff = tStaffExtendRepository.findById(tJoinExamShiftRequest.getSecondSupervisorId());
        if (existingStaff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Giám thị không tồn tại!");
        }

        if (tJoinExamShiftRequest.getSecondSupervisorId().equals(examShift.getFirstSupervisor().getId())
            && (examShift.getSecondSupervisor() == null
                || tJoinExamShiftRequest.getSecondSupervisorId().equals(examShift.getSecondSupervisor().getId()))) {
            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.OK, "Tham gia phòng thi thành công!");
        }

        examShift.setSecondSupervisor(existingStaff.get());
        tExamShiftExtendRepository.save(examShift);

        String accountFe = existingStaff.get().getAccountFe().split("@fe.edu.vn")[0];
        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT,
                new NotificationResponse("Giám thị " + accountFe + " đã tham gia phòng thi!"));

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(tExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
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

        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK,
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
        tStudentExamShiftExtendRepository.save(studentExamShift.get());

        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE,
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
        tStudentExamShiftExtendRepository.save(studentExamShift.get());

        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REFUSE,
                new NotificationResponse("Sinh viên " + student.get().getName() + " đã bị từ chối!"));

        return new ResponseObject<>(null, HttpStatus.OK,
                "Từ chối sinh viên " + student.get().getName() + " thành công!");
    }

    @Override
    public ResponseObject<?> startExamShift(String examShiftCode, String passwordExamPaperShift) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Phòng thi không tồn tại!");
            }

//            if (examShift.get().getSecondSupervisor() == null) {
//                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Phòng thi chưa đủ giám thị!");
//            }

            String departmentFacilityId
                    = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();
            String subjectId = examShift.get().getClassSubject().getSubject().getId();

            List<String> getListIdExamPaper
                    = tExamPaperBySemesterExtendRepository.getListIdExamPaper(departmentFacilityId, subjectId);

            Random random = new Random();
            int index = random.nextInt(getListIdExamPaper.size());
            String examPaperId = getListIdExamPaper.get(index);

            Optional<TExamPaperShiftResponse> examPaperShiftOptional
                    = tExamPaperShiftExtendRepository.findExamPaperShiftByExamShiftCode(examShiftCode);
            if (examPaperShiftOptional.isPresent()) {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Ca thi đã có đề thi rồi!");
            }

            ResponseObject<?> validatePassword = validatePassword(passwordExamPaperShift);
            if (validatePassword != null) {
                return validatePassword;
            }

            String salt = PasswordUtils.generateSalt();
            String password = PasswordUtils.getSecurePassword(passwordExamPaperShift, salt);

//            if (countStudentInExamShift(examShiftCode).getData().equals(0)) {
//                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Phòng thi không có sinh viên!");
//            }

            ExamPaperShift examPaperShift = new ExamPaperShift();
            examPaperShift.setExamShift(examShift.get());
            examPaperShift.setExamPaper(tExamPaperExtendRepository.getReferenceById(examPaperId));
            examPaperShift.setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
            examPaperShift.setStatus(EntityStatus.ACTIVE);
            long startTime = System.currentTimeMillis();
            long endTime = startTime + (2 * 60 * 1000);
            examPaperShift.setStartTime(startTime);
            examPaperShift.setEndTime(endTime);
            examPaperShift.setHash(password);
            examPaperShift.setSalt(salt);

            tExamPaperShiftExtendRepository.save(examPaperShift);

            examShift.get().setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);

            List<String> listStudentExamShiftId = tStudentExamShiftExtendRepository
                    .findAllStudentExamShiftIdByExamShiftCode(examShiftCode);
            for (String studentExamShiftId : listStudentExamShiftId) {
                StudentExamShift studentExamShift
                        = tStudentExamShiftExtendRepository.getReferenceById(studentExamShiftId);
                studentExamShift.setExamStudentStatus(ExamStudentStatus.IN_EXAM);
                tStudentExamShiftExtendRepository.save(studentExamShift);
            }

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT_START,
                    new NotificationResponse("Ca thi " + examShift.get().getExamShiftCode() + " đã bắt đầu!"));

            emailService.sendEmailWhenStartExamShift(tExamShiftExtendRepository.getHeadSubjectAndContentSendMail(examShiftCode));

            return new ResponseObject<>(
                    new TStartExamShiftResponse(
                            tExamPaperExtendRepository.getReferenceById(examPaperId).getPath()),
                    HttpStatus.OK, "Bắt đầu ca thi thành công!");

        } catch (Exception e) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST, "Phát sinh lỗi khi bắt đầu ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(tExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy path đề thi thành công!");
    }

    @Override
    public ResponseObject<?> getFile(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

        return new ResponseObject<>(
                new TFileResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK,
                "Lấy đề thi thành công!"
        );
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        Resource fileResponse = googleDriveFileService.loadFile(file);
        String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

        return new ResponseObject<>(
                new TExamRuleResourceResponse(data, fileResponse.getFilename()),
                HttpStatus.OK,
                "Lấy file quy định thi thành công!"
        );
    }

    @Override
    public ResponseObject<?> updateStatusExamShift(String examShiftCode) {
        Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
        if (examShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
        }
        ExamShift examShiftEntity = examShift.get();
        examShiftEntity.setExamShiftStatus(ExamShiftStatus.FINISHED);
        tExamShiftExtendRepository.save(examShiftEntity);
        return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật trạng thái ca thi thành công!");
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
        return tExamShiftExtendRepository.findByExamShiftCode(examShiftCode);
    }

    private Optional<Student> findStudentById(String studentId) {
        return tStudentExtendRepository.findById(studentId);
    }

    private Optional<StudentExamShift> findStudentExamShift(String examShiftId, String studentId) {
        return tStudentExamShiftExtendRepository.findByExamShiftIdAndStudentId(examShiftId, studentId);
    }

}
