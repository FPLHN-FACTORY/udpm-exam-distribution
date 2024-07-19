package fplhn.udpm.examdistribution.core.headdepartment.managehos.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.ModifySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectBySubjectGroupRequest;
import jakarta.validation.Valid;

public interface ManageRoleSubjectGroup {

    ResponseObject<?> getRoleSubjectGroup(RoleSubjectGroupRequest request);

    ResponseObject<?> createRoleSubjectGroup(@Valid ModifySubjectGroupRequest request);

    ResponseObject<?> updateRoleSubjectGroup(String subjectGroupId, @Valid ModifySubjectGroupRequest request);

    ResponseObject<?> getListSubjectBySubjectGroupId(SubjectBySubjectGroupRequest request);

}
