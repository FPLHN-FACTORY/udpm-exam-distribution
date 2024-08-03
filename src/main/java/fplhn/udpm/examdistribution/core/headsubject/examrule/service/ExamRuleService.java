package fplhn.udpm.examdistribution.core.headsubject.examrule.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.ChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.CreateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UpdateUploadExamRuleRequest;
import jakarta.validation.Valid;

public interface ExamRuleService {

    ResponseObject<?> getAllExamRule(FindExamRuleRequest request);

    ResponseObject<?> createExamRule(@Valid CreateUploadExamRuleRequest request);

    ResponseObject<?> getFile(String id);

    ResponseObject<?> getListSubject(FindSubjectRequest request);

    ResponseObject<?> chooseExamRule(ChooseExamRuleRequest request);

    ResponseObject<?> updateExamRule(UpdateUploadExamRuleRequest request);

}
