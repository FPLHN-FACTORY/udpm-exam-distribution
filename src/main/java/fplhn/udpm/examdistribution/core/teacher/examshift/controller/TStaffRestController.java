package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.teacher.examshift.service.TStaffService;
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
@RequestMapping(MappingConstants.API_TEACHER_STAFF)
public class TStaffRestController {

    private final TStaffService tStaffService;

    @GetMapping("/first-supervisor/{examShiftCode}")
    public ResponseEntity<?> findFirstSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tStaffService.findFirstSupervisorIdByExamShiftCode(examShiftCode));
    }

    @GetMapping("/second-supervisor/{examShiftCode}")
    public ResponseEntity<?> findSecondSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tStaffService.findSecondSupervisorIdByExamShiftCode(examShiftCode));
    }

}
