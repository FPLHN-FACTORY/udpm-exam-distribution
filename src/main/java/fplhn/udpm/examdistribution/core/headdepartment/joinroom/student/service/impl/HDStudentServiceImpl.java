package fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.repository.HDStudentExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.service.HDStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDStudentServiceImpl implements HDStudentService {

    private final HDStudentExtendRepository hdStudentExtendRepository;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hdStudentExtendRepository
                .findAllStudentByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên thành công!");
    }
}
