package fplhn.udpm.examdistribution.core.headsubject.joinroom.staff.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.staff.service.HSStaffService;
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
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_STAFF)
public class HSStaffRestController {

    private final HSStaffService hsStaffService;

    @GetMapping("/first-supervisor/{examShiftCode}")
    public ResponseEntity<?> findFirstSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hsStaffService.findFirstSupervisorIdByExamShiftCode(examShiftCode));
    }

    @GetMapping("/second-supervisor/{examShiftCode}")
    public ResponseEntity<?> findSecondSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hsStaffService.findSecondSupervisorIdByExamShiftCode(examShiftCode));
    }

}
