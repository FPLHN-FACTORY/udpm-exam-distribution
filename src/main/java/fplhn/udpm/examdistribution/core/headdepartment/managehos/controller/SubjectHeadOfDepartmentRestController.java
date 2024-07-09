package fplhn.udpm.examdistribution.core.headdepartment.managehos.controller;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ReassignHeadOfSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageSubjectService;
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
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_SUBJECT)
@RequiredArgsConstructor
public class SubjectHeadOfDepartmentRestController {

    private final ManageSubjectService manageSubjectService;

    @GetMapping
    public ResponseEntity<?> getSubjectsStaff(SubjectsStaffRequest request) {
        return createResponseEntity(manageSubjectService.getSubjectsStaff(request));
    }

    @GetMapping("/staffs-by-subject")
    public ResponseEntity<?> getStaffsBySubject(StaffsBySubjectRequest request) {
        return createResponseEntity(manageSubjectService.getStaffsBySubject(request));
    }

    @PostMapping("/reassign-subject")
    public ResponseEntity<?> reassignSubjectToStaff(@RequestBody ReassignHeadOfSubjectRequest request) {
        return createResponseEntity(manageSubjectService.reassignSubjectToStaff(request));
    }

}
