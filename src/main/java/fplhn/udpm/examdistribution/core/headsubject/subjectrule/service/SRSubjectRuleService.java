package fplhn.udpm.examdistribution.core.headsubject.subjectrule.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRUpdateExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRuleRequest;

public interface SRSubjectRuleService {

    ResponseObject<?> getFile(String id);

    ResponseObject<?> getListSubject(SRFindSubjectRequest request);

    ResponseObject<?> getListExamRule(SRFindSubjectRuleRequest request);

    ResponseObject<?> chooseExamRule(SRChooseExamRuleRequest request);

    ResponseObject<?> updateExamTime(SRUpdateExamTimeRequest request);

    ResponseObject<?> getExamTime(SRExamTimeRequest request);

    ResponseObject<?> allowOnlineSubject(String subjectId);

}

