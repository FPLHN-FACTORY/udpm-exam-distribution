package fplhn.udpm.examdistribution.core.headsubject.subjectrule.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;

public interface SRExamRuleService {

    ResponseObject<?> getFile(String id);

    ResponseObject<?> getListSubject(SRFindSubjectRequest request);

}
