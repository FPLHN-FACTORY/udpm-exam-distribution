package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSStudentExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSStudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSStudentServiceImpl implements HSStudentService {

    private final HSStudentExtendRepository hsStudentExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(
                    hsStudentExtendRepository.findAllStudentByExamShiftCode(examShiftCode, sessionHelper.getCurrentBlockId()),
                    HttpStatus.OK,
                    "Lấy thông tin danh sách sinh viên thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin danh sách sinh viên: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi không lấy được danh sách sinh viên!"
            );
        }
    }
}
