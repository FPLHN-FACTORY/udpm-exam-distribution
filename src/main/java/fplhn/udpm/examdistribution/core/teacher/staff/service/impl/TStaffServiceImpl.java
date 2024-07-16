package fplhn.udpm.examdistribution.core.teacher.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.staff.repository.TStaffExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.staff.service.TStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TStaffServiceImpl implements TStaffService {

    private final TStaffExtendRepository tStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(tStaffExtendRepository
                .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(tStaffExtendRepository
                .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
    }

}
