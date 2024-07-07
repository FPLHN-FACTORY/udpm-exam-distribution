package fplhn.udpm.examdistribution.core.headsubject.examapproval.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;

import java.io.IOException;

public interface EAExamPaperService {

    ResponseObject<?> getExamApprovals(EAExamPaperRequest request);

    ResponseObject<?> getSubjects(String departmentFacilityId,String staffId);

    ResponseObject<?> getFile(String path) throws IOException;

    ResponseObject<?> approvalExam(String examPaperId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    void cleanExamPaper();

}
