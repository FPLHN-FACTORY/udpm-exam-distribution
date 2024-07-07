package fplhn.udpm.examdistribution.core.headsubject.joinroom.student.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.student.repository.HSStudentExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.student.service.HSStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSStudentServiceImpl implements HSStudentService {

    private final HSStudentExtendRepository hsStudentExtendRepository;

    @Override
    public ResponseObject<?> findAllStudentByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hsStudentExtendRepository
                .findAllStudentByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin danh sách sinh viên thành công!");
    }
}
