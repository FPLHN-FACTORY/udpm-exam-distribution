package fplhn.udpm.examdistribution.core.headoffice.examrule.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOCreateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOUpdateUploadExamRuleRequest;
import jakarta.validation.Valid;

public interface HOExamRuleService {

    ResponseObject<?> getAllExamRule(HOFindExamRuleRequest request);

    ResponseObject<?> createExamRule(@Valid HOCreateUploadExamRuleRequest request);

    ResponseObject<?> getFile(String id);

    ResponseObject<?> getListSubject(HOFindSubjectRequest request);

    ResponseObject<?> chooseExamRule(HOChooseExamRuleRequest request);

    ResponseObject<?> updateExamRule(HOUpdateUploadExamRuleRequest request);

}
