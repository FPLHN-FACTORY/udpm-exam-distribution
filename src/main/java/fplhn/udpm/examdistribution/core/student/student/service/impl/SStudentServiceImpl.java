package fplhn.udpm.examdistribution.core.student.student.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.student.repository.SStudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.student.service.SStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SStudentServiceImpl implements SStudentService {

    private final SStudentExtendRepository sStudentExtendRepository;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(sStudentExtendRepository
                .findAllStudentByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên thành công!");
    }

    @Override
    public ResponseObject<?> findAllStudentRejoinByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(sStudentExtendRepository
                .findAllStudentRejoinByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên chờ phê duyệt thành công!");
    }
}
