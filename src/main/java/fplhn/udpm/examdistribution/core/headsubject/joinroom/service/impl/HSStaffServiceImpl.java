package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSStaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSStaffServiceImpl implements HSStaffService {

    private final HSStaffExtendRepository hsStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hsStaffExtendRepository
                .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        return new ResponseObject<>(hsStaffExtendRepository
                .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
    }

}
