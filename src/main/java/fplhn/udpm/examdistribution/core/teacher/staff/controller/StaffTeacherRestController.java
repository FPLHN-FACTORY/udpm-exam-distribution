package fplhn.udpm.examdistribution.core.teacher.staff.controller;

import fplhn.udpm.examdistribution.core.teacher.staff.service.StaffTeacherService;
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
public class StaffTeacherRestController {

    private final StaffTeacherService staffTeacherService;

    @GetMapping("/first-supervisor/{examShiftCode}")
    public ResponseEntity<?> findFirstSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(staffTeacherService.findFirstSupervisorIdByExamShiftCode(examShiftCode));
    }

    @GetMapping("/second-supervisor/{examShiftCode}")
    public ResponseEntity<?> findSecondSupervisorIdByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(staffTeacherService.findSecondSupervisorIdByExamShiftCode(examShiftCode));
    }

}
