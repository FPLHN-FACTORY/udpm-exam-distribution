package fplhn.udpm.examdistribution.core.headdepartment.managehos.controller;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageStaffHOSService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_HOS)
@RequiredArgsConstructor
public class HosRestController {

    private final ManageStaffHOSService manageStaffHOSService;

    @GetMapping
    public ResponseEntity<?> getStaffs(StaffRequest request) {
        return Helper.createResponseEntity(manageStaffHOSService.getAllStaffs(request));
    }

    @GetMapping("/subject-assigned")
    public ResponseEntity<?> getSubjectAssigned(SubjectAssignedRequest request) {
        return Helper.createResponseEntity(manageStaffHOSService.getSubjectAssigned(request));
    }

    @PostMapping("/assign-subject")
    public ResponseEntity<?> assignSubjectToStaff(@RequestBody AssignSubjectStaffRequest request) {
        return Helper.createResponseEntity(manageStaffHOSService.assignSubjectToStaff(request));
    }

}
