package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TApproveStudentWhenStartTime;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TExamShiftWhenStartExamRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TJoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamPaperShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TFileResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TStartExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TStartTimeExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamPaperBySemesterExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamPaperShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TStaffExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TStudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TStudentExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TExamShiftService;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.PasswordUtils;
import fplhn.udpm.examdistribution.utils.SessionHelper;
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
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class TExamShiftServiceImpl implements TExamShiftService {

    private final TStaffExtendRepository tStaffExtendRepository;

    private final TExamShiftExtendRepository tExamShiftExtendRepository;

    private final TStudentExamShiftExtendRepository tStudentExamShiftExtendRepository;

    private final TStudentExtendRepository tStudentExtendRepository;

    private final TExamPaperExtendRepository tExamPaperExtendRepository;

    private final TExamPaperShiftExtendRepository tExamPaperShiftExtendRepository;

    private final TExamPaperBySemesterExtendRepository tExamPaperBySemesterExtendRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final SessionHelper sessionHelper;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final RedisService redisService;

    @Override
    public boolean findUsersInExamShift(String examShiftCode) {
        Optional<ExamShift> examShift = tExamShiftExtendRepository.findByExamShiftCode(examShiftCode);
        if (examShift.isEmpty()) {
            return false;
        }

        boolean isCurrentUserSupervisor
                = examShift.get().getFirstSupervisor().getId()
                          .equals(sessionHelper.getCurrentUserId())
                  || (examShift.get().getSecondSupervisor() != null
                      && examShift.get().getSecondSupervisor().getId()
                              .equals(sessionHelper.getCurrentUserId()));

        if (!isCurrentUserSupervisor && sessionHelper.getCurrentUserRole().equals("GIANG_VIEN")) {
            return false;
        }

        return true;
    }

    @Override
    public ResponseObject<?> getAllExamShift() {
        try {
            Long currentDateWithoutTime = DateTimeUtil.getCurrentDateWithoutTime();
            String currentShiftString = Objects.requireNonNull(Shift.getCurrentShift()).toString();
            String supervisorId = sessionHelper.getCurrentUserId();
            return new ResponseObject<>(tExamShiftExtendRepository.findAllByExamDateAndShiftAndSupervisor(
                    currentDateWithoutTime, currentShiftString, supervisorId),
                    HttpStatus.OK, "Lấy tất cả ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy tất cả ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy tất cả ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        try {
            return new ResponseObject<>(tExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin ca thi!");
        }
    }

    @Override
    public ResponseObject<?> joinExamShift(@Valid TJoinExamShiftRequest tJoinExamShiftRequest) {
        try {
            Optional<ExamShift> existingExamShift = findExamShiftByCode(tJoinExamShiftRequest.getExamShiftCodeJoin());
            if (existingExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                        "Ca thi không tồn tại hoặc mật khẩu không đúng!");
            }

            ExamShift examShift = existingExamShift.get();

            if (examShift.getExamShiftStatus() == ExamShiftStatus.FINISHED) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi đã kết thúc!");
            }

            Optional<Staff> existingStaff = tStaffExtendRepository.findById(sessionHelper.getCurrentUserId());

            if (!sessionHelper.getCurrentUserId().equals(examShift.getFirstSupervisor().getId())
                && !sessionHelper.getCurrentUserId().equals(examShift.getSecondSupervisor().getId())) {
                return new ResponseObject<>(null,
                        HttpStatus.BAD_REQUEST, "Bạn không phải là giám thị trong ca thi này!");
            }

            String accountFe = existingStaff.get().getAccountFe().split("@fe.edu.vn")[0];
            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT,
                    new NotificationResponse("Giám thị " + accountFe + " đã tham gia ca thi! - " + tJoinExamShiftRequest.getExamShiftCodeJoin()));

            return new ResponseObject<>(examShift.getExamShiftCode(),
                    HttpStatus.OK, "Tham gia ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi tham gia ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi tham gia ca thi!");
        }
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        try {
            return new ResponseObject<>(tExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                    HttpStatus.OK, "Đếm số sinh viên trong ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi đếm số sinh viên trong ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi đếm số sinh viên trong ca thi!");
        }
    }

    @Override
    @Transactional
    public ResponseObject<?> removeStudent(String examShiftCode, String studentId, String reason) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }

            Optional<Student> student = findStudentById(studentId);
            if (student.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
            }

            Optional<StudentExamShift> studentExamShift
                    = findStudentExamShift(examShift.get().getId(), student.get().getId());
            if (studentExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Không có sinh viên trong ca thi này!");
            }

            studentExamShift.get().setExamStudentStatus(ExamStudentStatus.KICKED);
            studentExamShift.get().setCheckLogin(false);
            studentExamShift.get().setReason(reason);

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK,
                    new NotificationResponse("Sinh viên " + student.get().getName() + " đã bị kick ra khỏi ca thi! - "
                            + studentId + " - " + examShiftCode));

            return new ResponseObject<>(null, HttpStatus.OK, "Xoá sinh viên thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi xoá sinh viên: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi xoá sinh viên!");
        }
    }

    @Override
    public ResponseObject<?> approveStudent(String examShiftCode, String studentId) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }

            Optional<Student> student = findStudentById(studentId);
            if (student.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
            }

            Optional<StudentExamShift> studentExamShift
                    = findStudentExamShift(examShift.get().getId(), student.get().getId());
            if (studentExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Không có sinh viên trong ca thi này!");
            }

            studentExamShift.get().setExamStudentStatus(ExamStudentStatus.REGISTERED);
            studentExamShift.get().setCheckLogin(true);
            tStudentExamShiftExtendRepository.save(studentExamShift.get());

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE,
                    new NotificationResponse("Sinh viên " + student.get().getName() + " đã được phê duyệt vào ca thi! - "
                            + studentId + " - " + examShiftCode));

            return new ResponseObject<>(examShift.get().getExamShiftCode(), HttpStatus.OK,
                    "Phê duyệt sinh viên " + student.get().getName() + " thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi phê duyệt sinh viên: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi phê duyệt sinh viên!");
        }
    }

    @Override
    public ResponseObject<?> approveStudentWhenStartTime(String examShiftCode, String studentId,
                                                         TApproveStudentWhenStartTime tApproveStudentWhenStartTime) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }

            Optional<Student> student = findStudentById(studentId);
            if (student.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
            }

            Optional<StudentExamShift> studentExamShift
                    = findStudentExamShift(examShift.get().getId(), student.get().getId());
            if (studentExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Không có sinh viên trong ca thi này!");
            }

            String examPaperShiftId = tExamPaperShiftExtendRepository.findExamPaperShiftIdByExamShiftCode(examShiftCode);
            if (examPaperShiftId != null) {
                ExamPaperShift examPaperShift = tExamPaperShiftExtendRepository.getReferenceById(examPaperShiftId);
                if (examPaperShift.getStartTime() != null && examPaperShift.getEndTime() != null
                        && studentExamShift.get().getStartTime() == null && studentExamShift.get().getEndTime() == null
                        && studentExamShift.get().getExamStudentStatus().equals(ExamStudentStatus.REJOINED)) {
                    long startTime = System.currentTimeMillis();
                    Long examTime = tExamPaperShiftExtendRepository
                            .findExamTimeByExamShiftCode(examShiftCode, sessionHelper.getCurrentUserFacilityId());
                    if (examTime == null) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                                "Thời gian thi của môn này chưa có!");
                    }
                    if (tApproveStudentWhenStartTime.getExamTime()
                            > examTime || tApproveStudentWhenStartTime.getExamTime() <= 10) {
                        return new ResponseObject<>(null, HttpStatus.BAD_REQUEST,
                                "Thời gian thi của sinh viên này chỉ hợp lệ trong khoảng thời gian từ "
                                        + 10 + " đến " + examTime + " phút!");
                    }
                    long endTime = startTime + (tApproveStudentWhenStartTime.getExamTime() * 60 * 1000);
                    studentExamShift.get().setStartTime(startTime);
                    studentExamShift.get().setEndTime(endTime);
                    studentExamShift.get().setExamStudentStatus(ExamStudentStatus.IN_EXAM);
                    tStudentExamShiftExtendRepository.save(studentExamShift.get());
                    simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE,
                            new NotificationResponse("Sinh viên " + student.get().getName() + " đã được phê duyệt vào ca thi! - "
                                    + studentId + " - " + examShiftCode));

                    return new ResponseObject<>(examShift.get().getExamShiftCode(), HttpStatus.OK,
                            "Phê duyệt sinh viên " + student.get().getName() + " thành công!");
                }
            }

            studentExamShift.get().setExamStudentStatus(ExamStudentStatus.REGISTERED);
            tStudentExamShiftExtendRepository.save(studentExamShift.get());

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE,
                    new NotificationResponse("Sinh viên " + student.get().getName() + " đã được phê duyệt vào ca thi! - "
                            + studentId + " - " + examShiftCode));

            return new ResponseObject<>(examShift.get().getExamShiftCode(), HttpStatus.OK,
                    "Phê duyệt sinh viên " + student.get().getName() + " thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi phê duyệt sinh viên: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi phê duyệt sinh viên!");
        }
    }

    @Override
    public ResponseObject<?> refuseStudent(String examShiftCode, String studentId) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }

            Optional<Student> student = findStudentById(studentId);
            if (student.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Sinh viên không tồn tại!");
            }

            Optional<StudentExamShift> studentExamShift
                    = findStudentExamShift(examShift.get().getId(), student.get().getId());
            if (studentExamShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Không có sinh viên trong ca thi này!");
            }

            studentExamShift.get().setExamStudentStatus(ExamStudentStatus.KICKED);
            tStudentExamShiftExtendRepository.save(studentExamShift.get());

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REFUSE,
                    new NotificationResponse("Sinh viên " + student.get().getName() + " đã bị từ chối! - "
                            + studentId + " - " + examShiftCode));

            return new ResponseObject<>(null, HttpStatus.OK,
                    "Từ chối sinh viên " + student.get().getName() + " thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi từ chối sinh viên: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi từ chối sinh viên!");
        }
    }

    @Override
    public ResponseObject<?> startExamShift(String examShiftCode) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }

            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
            String subjectId = examShift.get().getClassSubject().getSubject().getId();

            if (countStudentInExamShift(examShiftCode).getData().equals(0)) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Ca thi không có sinh viên!");
            }

            String password = PasswordUtils.generatePassword();
            long startTime = System.currentTimeMillis();

            Optional<TExamPaperShiftResponse> examPaperShiftOptional
                    = tExamPaperShiftExtendRepository.findExamPaperShiftByExamShiftCode(examShiftCode);
            if (examPaperShiftOptional.isPresent()) {
                examShift.get().setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
                tExamShiftExtendRepository.save(examShift.get());

                ExamPaperShift examPaperShift
                        = tExamPaperShiftExtendRepository.getReferenceById(examPaperShiftOptional.get().getId());
                examPaperShift.setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
                examPaperShift.setPassword(password);
                tExamPaperShiftExtendRepository.save(examPaperShift);

                simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT_START,
                        new NotificationResponse(
                                "Ca thi " + examShift.get().getExamShiftCode() + " đã được phát đề! - "
                                        + examShiftCode
                        ));

                return new ResponseObject<>(
                        new TStartExamShiftResponse(
                                tExamPaperExtendRepository.getReferenceById(
                                        examPaperShiftOptional.get().getExamPaperId()).getPath(), password
                        ),
                        HttpStatus.OK, "Bắt đầu ca thi thành công!");
            }

            List<String> listIdExamPaper
                    = tExamPaperBySemesterExtendRepository
                    .getListIdExamPaper(departmentFacilityId, subjectId, sessionHelper.getCurrentSemesterId());

            if (listIdExamPaper.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không có đề thi nào!");
            }

            Random random = new Random();
            int index = random.nextInt(listIdExamPaper.size());
            String examPaperId = listIdExamPaper.get(index);

            ExamPaperShift examPaperShift = new ExamPaperShift();
            examPaperShift.setExamShift(examShift.get());
            examPaperShift.setExamPaper(tExamPaperExtendRepository.getReferenceById(examPaperId));
            examPaperShift.setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
            examPaperShift.setStatus(EntityStatus.ACTIVE);
            examPaperShift.setPassword(password);
            tExamPaperShiftExtendRepository.save(examPaperShift);

            examShift.get().setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
            tExamShiftExtendRepository.save(examShift.get());

            TExamShiftWhenStartExamRequest tExamShiftWhenStartExamRequest = new TExamShiftWhenStartExamRequest();
            tExamShiftWhenStartExamRequest.setExamShiftCode(examShiftCode);
            tExamShiftWhenStartExamRequest.setExamDate(examShift.get().getExamDate());
            tExamShiftWhenStartExamRequest.setShift(String.valueOf(examShift.get().getShift()));
            tExamShiftWhenStartExamRequest.setSubjectId(subjectId);
            tExamShiftWhenStartExamRequest.setBlockId(examShift.get().getClassSubject().getBlock().getId());
            tExamShiftWhenStartExamRequest.setDepartmentFacilityId(departmentFacilityId);

            List<String> listExamShiftId
                    = tExamShiftExtendRepository.getExamShiftIdWhenStartExam(tExamShiftWhenStartExamRequest);

            for (String examShiftId : listExamShiftId) {
                ExamShift examShiftEntity = tExamShiftExtendRepository.getReferenceById(examShiftId);
                ExamPaperShift examPaperShiftEntity = new ExamPaperShift();
                examPaperShiftEntity.setExamShift(examShiftEntity);
                examPaperShiftEntity.setExamPaper(tExamPaperExtendRepository.getReferenceById(examPaperId));
                examPaperShiftEntity.setStartTime(startTime + (120 * 60 * 1000));
                tExamPaperShiftExtendRepository.save(examPaperShiftEntity);
            }

            simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT_START,
                    new NotificationResponse(
                            "Ca thi " + examShift.get().getExamShiftCode() + " đã được phát đề! - "
                                    + examShiftCode
                    ));

            return new ResponseObject<>(
                    new TStartExamShiftResponse(
                            tExamPaperExtendRepository.getReferenceById(examPaperId).getPath(), password),
                    HttpStatus.OK, "Phát đề thi thành công!");

        } catch (Exception e) {
            log.error("Lỗi khi phát đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi phát đề thi!");
        }
    }

    @Override
    public ResponseObject<?> startTime(String examShiftCode) {
        try {
            long startTime = System.currentTimeMillis();
            Long examTime = tExamPaperShiftExtendRepository.findExamTimeByExamShiftCode(examShiftCode,
                    sessionHelper.getCurrentUserFacilityId());
            if (examTime == null) {
                return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Thời gian thi của môn này chưa có!");
            }
            long endTime = startTime + (examTime * 60 * 1000);

            Optional<TExamPaperShiftResponse> examPaperShiftOptional
                    = tExamPaperShiftExtendRepository.findExamPaperShiftByExamShiftCode(examShiftCode);
            if (examPaperShiftOptional.isPresent()) {
                ExamPaperShift examPaperShift
                        = tExamPaperShiftExtendRepository.getReferenceById(examPaperShiftOptional.get().getId());
                examPaperShift.setStartTime(startTime);
                examPaperShift.setEndTime(endTime);
                examPaperShift.setExamShiftStatus(ExamShiftStatus.IN_PROGRESS);
                tExamPaperShiftExtendRepository.save(examPaperShift);

                List<String> listStudentExamShiftId = tStudentExamShiftExtendRepository
                        .findAllStudentExamShiftIdByExamShiftCode(examShiftCode);
                for (String studentExamShiftId : listStudentExamShiftId) {
                    StudentExamShift studentExamShift
                            = tStudentExamShiftExtendRepository.getReferenceById(studentExamShiftId);
                    studentExamShift.setExamStudentStatus(ExamStudentStatus.IN_EXAM);
                    studentExamShift.setStartTime(startTime);
                    studentExamShift.setEndTime(endTime);
                    tStudentExamShiftExtendRepository.save(studentExamShift);
                }

                simpMessagingTemplate.convertAndSend(TopicConstant.TOPIC_EXAM_SHIFT_START_TIME,
                        new NotificationResponse(
                                "Ca thi bắt đầu tính giờ! - "
                                        + examShiftCode
                        ));
            }
            return new ResponseObject<>(new TStartTimeExamShiftResponse(startTime, endTime),
                    HttpStatus.OK, "Bắt đầu ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi bắt đầu ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi bắt đầu ca thi!");
        }
    }

    @Override
    public ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(
                    tExamPaperExtendRepository.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy path đề thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy path đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy path đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getFile(String file) throws IOException {
        try {
            Optional<ExamPaper> examPaperOptional = tExamPaperExtendRepository.findExamPaperByPath(file);
            if (examPaperOptional.isEmpty()) {
                return new ResponseObject<>(
                        "",
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi"
                );
            }

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_RULE + examPaperOptional.get().getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new TFileResourceResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Lấy đề thi thành công!"
                );
            }

            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new TFileResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "Lấy đề thi thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy đề thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy đề thi!");
        }
    }

    @Override
    public ResponseObject<?> getFileExamRule(String file) throws IOException {
        try {
            Resource fileResponse = googleDriveFileService.loadFile(file);
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());

            return new ResponseObject<>(
                    new TExamRuleResourceResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "Lấy file quy định thi thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy file quy định thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy file quy định thi!");
        }
    }

    @Override
    public ResponseObject<?> updateStatusExamShift(String examShiftCode) {
        try {
            Optional<ExamShift> examShift = findExamShiftByCode(examShiftCode);
            if (examShift.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Ca thi không tồn tại!");
            }
            ExamShift examShiftEntity = examShift.get();
            examShiftEntity.setExamShiftStatus(ExamShiftStatus.FINISHED);
            tExamShiftExtendRepository.save(examShiftEntity);
            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật trạng thái ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái ca thi: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi cập nhật trạng thái ca thi!");
        }
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
