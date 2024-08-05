package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDStudentExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDStudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HDStudentServiceImpl implements HDStudentService {

    private final HDStudentExtendRepository hdStudentExtendRepository;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        try {
            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            return new ResponseObject<>(
                    hdStudentExtendRepository.findAllStudentByExamShiftCode(examShiftCode, blockId),
                    HttpStatus.OK,
                    "Lấy thông tin danh sách sinh viên thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách sinh viên: ", e);
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lỗi khi lấy danh sách sinh viên!"
            );
        }
    }
}
