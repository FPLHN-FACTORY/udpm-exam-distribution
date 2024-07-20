package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.CreateSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.ModifySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectBySubjectGroupRequest;
import jakarta.validation.Valid;

public interface ManageRoleSubjectGroup {

    ResponseObject<?> getRoleSubjectGroup(@Valid RoleSubjectGroupRequest request);

    ResponseObject<?> createRoleSubjectGroup(@Valid CreateSubjectGroupRequest request);

    ResponseObject<?> updateRoleSubjectGroup(String subjectGroupId, @Valid ModifySubjectGroupRequest request);

    ResponseObject<?> getListSubjectBySubjectGroupId(SubjectBySubjectGroupRequest request);

}
