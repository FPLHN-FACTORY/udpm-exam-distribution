package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.AddExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import jakarta.validation.Valid;

public interface UploadExamPaperService {

    ResponseObject<?> getListSubject();

    ResponseObject<?> getAllExamPaper(ListExamPaperRequest request);

    ResponseObject<?> getFile(String fileId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> getListMajorFacility();

    ResponseObject<?> addExamPaper(@Valid AddExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid UpdateExamPaperRequest request);

}
