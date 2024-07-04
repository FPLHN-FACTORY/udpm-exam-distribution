package fplhn.udpm.examdistribution.core.headsubject.assignuploader.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindSubjectRequest;

public interface AssignUploaderService {

    ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request);

    ResponseObject<?> getAllStaff(String departmentFacilityId, FindStaffRequest request);

}
