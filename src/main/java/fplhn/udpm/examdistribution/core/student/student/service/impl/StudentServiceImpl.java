package fplhn.udpm.examdistribution.core.student.student.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.student.repository.StudentExtendRepository;
import fplhn.udpm.examdistribution.core.student.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentExtendRepository studentExtendRepository;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(studentExtendRepository
                .findAllStudentByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên thành công!");
    }
}
