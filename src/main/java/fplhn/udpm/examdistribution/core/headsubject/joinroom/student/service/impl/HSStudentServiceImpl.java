package fplhn.udpm.examdistribution.core.headsubject.joinroom.student.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.student.repository.HSStudentExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.student.service.HSStudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSStudentServiceImpl implements HSStudentService {

    private final HSStudentExtendRepository hsStudentExtendRepository;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        try {
            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            return new ResponseObject<>(
                    hsStudentExtendRepository.findAllStudentByExamShiftCode(examShiftCode, blockId),
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
}
