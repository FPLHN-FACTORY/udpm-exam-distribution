package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HDStaffServiceImpl implements HDStaffService {

    private final HDSStaffExtendRepository hdsStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hdsStaffExtendRepository
                    .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 1: {}", e.getMessage());
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 1");
        }
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hdsStaffExtendRepository
                    .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 2: {}", e.getMessage());
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 2");
        }
    }

}
