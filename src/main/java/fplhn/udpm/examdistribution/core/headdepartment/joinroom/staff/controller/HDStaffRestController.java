package fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.staff.service.HDStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_STAFF)
public class HDStaffRestController {

    private final HDStaffService hdStaffService;

    @GetMapping("/first-supervisor/{examShiftCode}")
    public ResponseEntity<?> findFirstSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hdStaffService.findFirstSupervisorIdByExamShiftCode(examShiftCode));
    }

    @GetMapping("/second-supervisor/{examShiftCode}")
    public ResponseEntity<?> findSecondSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hdStaffService.findSecondSupervisorIdByExamShiftCode(examShiftCode));
    }

}
