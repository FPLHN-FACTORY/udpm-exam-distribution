package fplhn.udpm.examdistribution.core.headsubject.examrule.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UploadExamRuleRequest;
import org.springframework.core.io.Resource;

import java.io.IOException;

public interface ExamRuleService {

    ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request);

    ResponseObject<?> uploadExamRule(String subjectId, UploadExamRuleRequest request);

    ResponseObject<?> getFile(GetFileRequest request) throws IOException;

}