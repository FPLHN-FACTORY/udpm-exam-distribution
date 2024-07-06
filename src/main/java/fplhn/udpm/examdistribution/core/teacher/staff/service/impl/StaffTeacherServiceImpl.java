package fplhn.udpm.examdistribution.core.teacher.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.staff.repository.StaffTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.staff.service.StaffTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StaffTeacherServiceImpl implements StaffTeacherService {

    private final StaffTeacherExtendRepository staffTeacherExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(staffTeacherExtendRepository
                .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(staffTeacherExtendRepository
                .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
    }

}
