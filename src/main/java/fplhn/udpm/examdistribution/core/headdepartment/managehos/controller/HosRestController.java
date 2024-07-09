package fplhn.udpm.examdistribution.core.headdepartment.managehos.controller;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.AssignSubjectStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageHeadSubjectService;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.impl.CommonHeadOfDepartmentService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fplhn.udpm.examdistribution.utils.Helper.createResponseEntity;

@RestController
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_HOS)
@RequiredArgsConstructor
public class HosRestController {

    private final ManageHeadSubjectService manageHeadSubjectService;

    private final CommonHeadOfDepartmentService commonHeadOfDepartmentService;

    @GetMapping
    public ResponseEntity<?> getStaffs(HeadSubjectRequest request) {
        return createResponseEntity(manageHeadSubjectService.getStaffAndHeadSubjects(request));
    }

    @GetMapping("/subject-assigned")
    public ResponseEntity<?> getSubjectAssigned(SubjectAssignedRequest request) {
        return createResponseEntity(manageHeadSubjectService.getSubjectAssigned(request));
    }

    @PostMapping("/subject-assigned")
    public ResponseEntity<?> assignSubjectToStaff(@RequestBody AssignSubjectStaffRequest request) {
        return createResponseEntity(manageHeadSubjectService.assignSubjectToStaff(request));
    }

    @GetMapping("/semester")
    public ResponseEntity<?> getSemesterInfo() {
        return createResponseEntity(commonHeadOfDepartmentService.getSemesterInfo());
    }

}
