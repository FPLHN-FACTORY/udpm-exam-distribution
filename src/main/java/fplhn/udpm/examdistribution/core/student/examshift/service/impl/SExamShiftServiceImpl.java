package fplhn.udpm.examdistribution.core.student.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SOpenExamPaperRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SRefreshJoinRoomRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.response.SExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.student.examshift.model.response.SFileResourceResponse;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SExamPaperShiftRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.service.SExamShiftService;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamPaperShiftExtendRepository;
import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.SessionHelper;
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
import java.util.Date;
import java.util.Objects;
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

    private final TExamPaperShiftExtendRepository tExamPaperShiftExtendRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final SessionHelper sessionHelper;

    @Override
    public boolean findStudentInExamShift(String examShiftCode) {
        Optional<ExamShift> existingExamShift = sExamShiftExtendRepository
                .findByExamShiftCode(examShiftCode);
        if (existingExamShift.isEmpty()) {
            return false;
        }

        Optional<StudentExamShift> studentExamShift = sStudentExamShiftExtendRepository
                .findByExamShiftIdAndStudentId(existingExamShift.get().getId(), sessionHelper.getCurrentUserId());

        if (studentExamShift.isEmpty()
                || (studentExamShift.get().getExamStudentStatus().toString().matches("DONE_EXAM|KICKED|REJOINED")
                && existingExamShift.get().getExamShiftStatus() == ExamShiftStatus.FINISHED)
                || !studentExamShift.get().isCheckLogin()
        ) {
            return false;
        }

        return true;
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid SExamShiftRequest sExamShiftRequest) {
        try {
            Optional<Student> existingStudent = studentExtendRepository.findById(sExamShiftRequest.getStudentId());
            if (existingStudent.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sinh viên không tồn tại trong hệ thống!");
            }

            Optional<ExamShift> existingExamShift = sExamShiftExtendRepository
                    .findByExamShiftCode(sExamShiftRequest.getExamShiftCodeJoin());
            if (existingExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không tồn tại hoặc mật khẩu không đúng!");
            }

            ExamShift examShift = existingExamShift.get();

            if (!examShift.getPassword().equals(sExamShiftRequest.getPasswordJoin())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không tồn tại hoặc mật khẩu không đúng!");
            }

            if (examShift.getExamShiftStatus().equals(ExamShiftStatus.FINISHED)) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi đã kết thúc!");
            }

            if (!(examShift.getShift().equals(Shift.getCurrentShift()))
                    || !(Objects.equals(examShift.getExamDate(), DateTimeUtil.getCurrentDateWithoutTime()))) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không tồn tại hoặc mật khẩu không đúng!");
            }

            Optional<StudentExamShift> studentExamShiftExist = sStudentExamShiftExtendRepository
                    .findByExamShiftIdAndStudentId(examShift.getId(), sExamShiftRequest.getStudentId());
            if (studentExamShiftExist.isPresent()) {
                if (studentExamShiftExist.get().isCheckLogin()) {
                    return new ResponseObject<>(
                            null,
                            HttpStatus.NOT_ACCEPTABLE,
                            "Bạn đang trong phòng thi không được phép vào!"
                    );
                }
                ExamStudentStatus examStudentStatus = studentExamShiftExist.get().getExamStudentStatus();
                if ((examShift.getExamShiftStatus().equals(ExamShiftStatus.IN_PROGRESS)
                        || examShift.getExamShiftStatus().equals(ExamShiftStatus.NOT_STARTED))
                        && (examStudentStatus.equals(ExamStudentStatus.KICKED)
                        || examStudentStatus.equals(ExamStudentStatus.REJOINED))) {
                    studentExamShiftExist.get().setExamStudentStatus(ExamStudentStatus.REJOINED);
                    sStudentExamShiftExtendRepository.save(studentExamShiftExist.get());
                    simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN,
                            new NotificationResponse(
                                    "Sinh viên "
                                            + existingStudent.get().getStudentCode()
                                            + " yêu cầu tham gia ca thi! - "
                                            + existingExamShift.get().getExamShiftCode()
                                            + " - " + sExamShiftRequest.getStudentId()
                            ));
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
                    studentExamShift.setCheckLogin(true);
                    sStudentExamShiftExtendRepository.save(studentExamShift);
                    simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_REMOVE_TAB_REJOIN,
                            new NotificationResponse(
                                    studentExamShiftExist.get().getExamShift().getExamShiftCode()
                            )
                    );
                    return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                            HttpStatus.OK, "Tham gia ca thi thành công!");
                }

            } else {
                String examPaperShiftId = tExamPaperShiftExtendRepository
                        .findExamPaperShiftIdByExamShiftCode(examShift.getExamShiftCode());
                if (examPaperShiftId != null) {
                    ExamPaperShift examPaperShift = tExamPaperShiftExtendRepository.getReferenceById(examPaperShiftId);
                    if (examPaperShift.getStartTime() == null
                            && examPaperShift.getExamShiftStatus().equals(ExamShiftStatus.IN_PROGRESS)) {
                        StudentExamShift studentExamShift = new StudentExamShift();
                        studentExamShift.setStudent(existingStudent.get());
                        studentExamShift.setExamShift(examShift);
                        studentExamShift.setJoinTime(sExamShiftRequest.getJoinTime());
                        studentExamShift.setExamStudentStatus(ExamStudentStatus.REJOINED);
                        studentExamShift.setStatus(EntityStatus.ACTIVE);
                        sStudentExamShiftExtendRepository.save(studentExamShift);
                        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN,
                                new NotificationResponse(
                                        "Sinh viên "
                                                + existingStudent.get().getStudentCode()
                                                + " yêu cầu tham gia ca thi! - "
                                                + existingExamShift.get().getExamShiftCode()
                                                + " - " + sExamShiftRequest.getStudentId()
                                ));
                        return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                                HttpStatus.OK, "Vui lòng chờ giám thị phê duyệt!");
                    }
                    if (examPaperShift.getStartTime() != null && examPaperShift.getExamShiftStatus().equals(ExamShiftStatus.IN_PROGRESS)) {
                        StudentExamShift studentExamShift = new StudentExamShift();
                        studentExamShift.setStudent(existingStudent.get());
                        studentExamShift.setExamShift(examShift);
                        studentExamShift.setJoinTime(sExamShiftRequest.getJoinTime());
                        studentExamShift.setExamStudentStatus(ExamStudentStatus.REJOINED);
                        studentExamShift.setStatus(EntityStatus.ACTIVE);
                        sStudentExamShiftExtendRepository.save(studentExamShift);
                        simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN,
                                new NotificationResponse(
                                        "Sinh viên "
                                                + existingStudent.get().getStudentCode()
                                                + " yêu cầu tham gia ca thi! - "
                                                + existingExamShift.get().getExamShiftCode()
                                                + " - " + sExamShiftRequest.getStudentId()
                                ));
                        return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                                HttpStatus.OK, "Vui lòng chờ giám thị phê duyệt!");
                    }
                }

                StudentExamShift studentExamShift = new StudentExamShift();
                studentExamShift.setStudent(existingStudent.get());
                studentExamShift.setExamShift(examShift);
                studentExamShift.setJoinTime(sExamShiftRequest.getJoinTime());
                studentExamShift.setExamStudentStatus(ExamStudentStatus.REGISTERED);
                studentExamShift.setStatus(EntityStatus.ACTIVE);
                studentExamShift.setCheckLogin(true);
                sStudentExamShiftExtendRepository.save(studentExamShift);

                simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT,
                        new NotificationResponse(
                                "Sinh viên "
                                        + existingStudent.get().getStudentCode()
                                        + " đã tham gia ca thi! - "
                                        + existingExamShift.get().getExamShiftCode()));
            }

            return new ResponseObject<>(existingExamShift.get().getExamShiftCode(),
                    HttpStatus.OK, "Tham gia ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tham gia ca thi: ", e);
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi tham gia ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        try {
            return new ResponseObject<>(sExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        try {
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

            return new ResponseObject<>(
                    new SExamRuleResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "Lấy file quy định thi thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy file quy định thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy file quy định thi!"
            );
        }
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        try {
            System.out.println(sessionHelper.getCurrentUserId());
            return new ResponseObject<>(sExamPaperExtendRepository
                    .getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode, sessionHelper.getCurrentUserId()),
                    HttpStatus.OK, "Lấy path đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy path đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy path đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) {
        try {
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            return new ResponseObject<>(
                    new SFileResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK, "Lấy đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy path đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getStartTimeEndTimeExamPaperByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(sExamPaperExtendRepository.getStartTimeEndTimeExamPaperByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thời gian bắt đầu và kết thúc đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thời gian bắt đầu và kết thúc đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thời gian bắt đầu và kết thúc đề thi!");
        }
    }

    @Override
    public ResponseObject<?> openExamPaper(SOpenExamPaperRequest sOpenExamPaperRequest) {
        try {
            ExamPaperShift examPaperShift
                    = sExamPaperShiftRepository.getReferenceById(sOpenExamPaperRequest.getExamPaperShiftId());

            if (!examPaperShift.getPassword().equals(sOpenExamPaperRequest.getPasswordOpen())) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Mật khẩu không đúng!");
            }

            return new ResponseObject<>(null, HttpStatus.OK, "Mở đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi mở đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi mở đề thi!");
        }
    }

    @Override
    public ResponseObject<?> updateExamStudentStatus(String examShiftCode) {
        try {
            Optional<ExamShift> existingExamShift = sExamShiftExtendRepository
                    .findByExamShiftCode(examShiftCode);
            if (existingExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Ca thi không tồn tại!");
            }

            Optional<StudentExamShift> studentExamShift = sStudentExamShiftExtendRepository
                    .findByExamShiftIdAndStudentId(existingExamShift.get().getId(), sessionHelper.getCurrentUserId());
            if (studentExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Sinh viên không tồn tại trong ca thi!");
            }

            StudentExamShift studentExamShiftUpdate = studentExamShift.get();
            studentExamShiftUpdate.setExamStudentStatus(ExamStudentStatus.DONE_EXAM);
            sStudentExamShiftExtendRepository.save(studentExamShiftUpdate);

            return new ResponseObject<>(null, HttpStatus.OK,
                    "Cập nhật trạng thái sinh viên trong ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái sinh viên trong ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi cập nhật trạng thái sinh viên trong ca thi!");
        }
    }

    @Override
    public ResponseObject<?> refreshJoinRoom(SRefreshJoinRoomRequest request) {
        Optional<StudentExamShift> studentExamShiftOptional = sStudentExamShiftExtendRepository.findByExamShiftCodeAndStudentId(
                request.getExamShiftCode(),
                request.getStudentId()
        );
        if (studentExamShiftOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy học sinh trong phòng thi"
            );
        }

        if (!studentExamShiftOptional.get().getExamShift().getExamShiftStatus().equals(ExamShiftStatus.FINISHED)) {
            StudentExamShift studentExamShift = studentExamShiftOptional.get();
            studentExamShift.setCheckLogin(false);
            studentExamShift.setLeaveTime(new Date().getTime());
            sStudentExamShiftExtendRepository.save(studentExamShift);

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_REMOVE_TAB,
                    new NotificationResponse(
                            studentExamShiftOptional.get().getExamShift().getExamShiftCode()
                    )
            );
        }

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Cập nhật trạng thái vào phòng thi thành công"
        );
    }

}
