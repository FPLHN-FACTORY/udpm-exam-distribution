package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindStaffRequest;

public interface AssignUploaderService {

    ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request);

    ResponseObject<?> getAllStaff(String departmentFacilityId, FindStaffRequest request);

    ResponseObject<?> addOrDelAssignUploader(AssignUploaderRequest request);

}
