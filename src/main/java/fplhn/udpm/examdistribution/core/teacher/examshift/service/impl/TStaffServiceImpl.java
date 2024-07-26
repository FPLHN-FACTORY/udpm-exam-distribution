package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TStaffExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TStaffServiceImpl implements TStaffService {

    private final TStaffExtendRepository tStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(tStaffExtendRepository
                    .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 1: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 1!");
        }
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(tStaffExtendRepository
                    .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 2: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 2!");
        }
    }

}
