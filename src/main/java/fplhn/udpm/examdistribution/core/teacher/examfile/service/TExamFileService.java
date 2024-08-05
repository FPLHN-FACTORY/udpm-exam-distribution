package fplhn.udpm.examdistribution.core.teacher.examfile.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;

public interface TExamFileService {

    ResponseObject<?> getAllSubject(TFindSubjectRequest request);

    ResponseObject<?> getSubjectById(String subjectId);

    ResponseObject<?> uploadExamRule(String subjectId, TUploadExamFileRequest request);

    ResponseObject<?> getMajorFacilityByDepartmentFacility();

    ResponseObject<?> getSampleExamPaper(String subjectId);

    ResponseObject<?> getExamPapers(TExamFileRequest request);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> getExamPaper(String examPaperId);

    ResponseObject<?> getCount(String subjectId);

    void cleanExamPaper();

}
