package fplhn.udpm.examdistribution.core.teacher.examfile.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;

import java.io.IOException;


public interface TExamFileService {

    ResponseObject<?> getAllSubject(String departmentFacilityId, TFindSubjectRequest request);

    ResponseObject<?> uploadExamRule(String subjectId, TUploadExamFileRequest request);

    ResponseObject<?> getMajorFacilityByDepartmentFacility(String departmentFacilityId);

    ResponseObject<?> getSampleExamPaper(String subjectId);

}
