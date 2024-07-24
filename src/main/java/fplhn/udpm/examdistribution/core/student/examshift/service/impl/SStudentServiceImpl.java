package fplhn.udpm.examdistribution.core.student.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.repository.SStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.examshift.service.SStudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SStudentServiceImpl implements SStudentService {

    private final SStudentExtendRepository sStudentExtendRepository;

    private final HttpSession httpSession;

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
}
