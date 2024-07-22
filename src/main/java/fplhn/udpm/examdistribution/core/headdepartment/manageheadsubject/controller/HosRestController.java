package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.controller;

import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.AssignSubjectGroupToStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectGroupAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.ManageHeadSubjectService;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service.impl.CommonHeadOfDepartmentService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("/subject-group")
    public ResponseEntity<?> getSubjectAssigned(SubjectGroupAssignedRequest request) {
        return createResponseEntity(manageHeadSubjectService.getSubjectGroupAssigned(request));
    }

    @PostMapping("/subject-assigned")
    public ResponseEntity<?> assignSubjectToStaff(@RequestBody AssignSubjectGroupToStaffRequest request) {
        return createResponseEntity(manageHeadSubjectService.assignSubjectGroupToStaff(request));
    }

    @GetMapping("/semester")
    public ResponseEntity<?> getSemesterInfo() {
        return createResponseEntity(commonHeadOfDepartmentService.getSemesterInfo());
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        return createResponseEntity(manageHeadSubjectService.getChangeHistory(page, size));
    }

}