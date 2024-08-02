package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSStaffService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSStaffServiceImpl implements HSStaffService {

    private final HSStaffExtendRepository hsStaffExtendRepository;

    @Override
    public ResponseObject<?> findFirstSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hsStaffExtendRepository
                    .findFirstSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 1 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 1: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 1!");
        }
    }

    @Override
    public ResponseObject<?> findSecondSupervisorIdByExamShiftCode(String examShiftCode) {
        try {
            return new ResponseObject<>(hsStaffExtendRepository
                    .findSecondSupervisorIdByExamShiftCode(examShiftCode),
                    HttpStatus.OK, "Lấy thông tin giám thị 2 thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin giám thị 2: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy thông tin giám thị 2!");
        }
    }

}
