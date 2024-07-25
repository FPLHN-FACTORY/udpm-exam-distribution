package fplhn.udpm.examdistribution.core.student.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExamShiftTrackExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.service.SStudentService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.entity.StudentExamShiftTrack;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SStudentServiceImpl implements SStudentService {

    private final SStudentExtendRepository sStudentExtendRepository;

    private final SExamShiftExtendRepository sExamShiftExtendRepository;

    private final SStudentExamShiftExtendRepository sStudentExamShiftExtendRepository;

    private final SStudentExamShiftTrackExtendRepository sStudentExamShiftTrackExtendRepository;

    private final HttpSession httpSession;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        try {
            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            return new ResponseObject<>(
                    sStudentExtendRepository.findAllStudentByExamShiftCode(examShiftCode, blockId),
                    HttpStatus.OK,
                    "Lấy thông tin danh sách sinh viên thành công!"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lỗi không lấy được danh sách sinh viên"
            );
        }
    }

    @Override
    public ResponseObject<?> findAllStudentRejoinByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(sStudentExtendRepository
                .findAllStudentRejoinByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên chờ phê duyệt thành công!");
    }

    @Override
    public ResponseObject<?> kickStudentUnInstallExt(String examShiftCode) {
        Optional<Student> studentOptional = sStudentExtendRepository.findStudentExist(sessionHelper.getCurrentUserId());

        if (studentOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy sinh viên này!"
            );
        }

        Optional<ExamShift> examShiftOptional = sExamShiftExtendRepository.findExamShiftByCode(
                examShiftCode,
                sessionHelper.getCurrentSemesterId(),
                sessionHelper.getCurrentUserFacilityId()
        );
        if (examShiftOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy phòng thi này!"
            );
        }

        Optional<StudentExamShift> studentExamShiftOptional = sStudentExamShiftExtendRepository.findByExamShiftCodeAndStudentIdAndFacilityId(
                examShiftCode,
                sessionHelper.getCurrentUserId(),
                sessionHelper.getCurrentUserFacilityId(),
                sessionHelper.getCurrentSemesterId()
        );
        if (studentExamShiftOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy sinh viên này trong phòng thi!"
            );
        }


        StudentExamShift studentExamShift = studentExamShiftOptional.get();
        studentExamShift.setExamStudentStatus(ExamStudentStatus.KICKED);
        studentExamShift.setReason("Sinh viên tắt extension");
        sStudentExamShiftExtendRepository.save(studentExamShift);

        StudentExamShiftTrack studentExamShiftTrack = new StudentExamShiftTrack();
        studentExamShiftTrack.setStudent(studentOptional.get());
        studentExamShiftTrack.setExamShift(examShiftOptional.get());
        studentExamShiftTrack.setUrl("Sinh viên tắt extension");
        studentExamShiftTrack.setTimeViolation(new Date().getTime());
        sStudentExamShiftTrackExtendRepository.save(studentExamShiftTrack);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Kích sinh viên không bật ext thành công!"
        );
    }
}
