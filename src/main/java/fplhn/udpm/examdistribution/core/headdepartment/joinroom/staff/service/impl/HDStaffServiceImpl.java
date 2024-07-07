package fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.repository.HDSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.service.HDStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDStaffServiceImpl implements HDStaffService {

    private final HDSStaffExtendRepository hdsStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hdsStaffExtendRepository
                .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hdsStaffExtendRepository
                .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
    }

}
