package fplhn.udpm.examdistribution.core.headdepartment.managehos.controller;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ModifySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectBySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.service.ManageRoleSubjectGroup;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP)
@RequiredArgsConstructor
public class SubjectGroupRestController {

    private final ManageRoleSubjectGroup manageRoleSubjectGroup;

    @GetMapping
    public ResponseEntity<?> getRoleSubjectGroup(RoleSubjectGroupRequest request) {
        return Helper.createResponseEntity(manageRoleSubjectGroup.getRoleSubjectGroup(request));
    }

    @PostMapping
    public ResponseEntity<?> createRoleSubjectGroup(ModifySubjectGroupRequest request) {
        return Helper.createResponseEntity(manageRoleSubjectGroup.createRoleSubjectGroup(request));
    }

    @PutMapping("/{subjectGroupId}")
    public ResponseEntity<?> updateRoleSubjectGroup(@PathVariable String subjectGroupId, ModifySubjectGroupRequest request) {
        return Helper.createResponseEntity(manageRoleSubjectGroup.updateRoleSubjectGroup(subjectGroupId, request));
    }

    @GetMapping("/subject")
    public ResponseEntity<?> getListSubjectBySubjectGroupId(SubjectBySubjectGroupRequest request) {
        return Helper.createResponseEntity(manageRoleSubjectGroup.getListSubjectBySubjectGroupId(request));
    }

}
