package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import jakarta.validation.Valid;

public interface UploadExamPaperService {

    ResponseObject<?> getListSubject(String semesterId);

    ResponseObject<?> getListCurrentSubject();

    ResponseObject<?> getListSemester();

    ResponseObject<?> getListBlock(String semesterId);

    ResponseObject<?> getListStaff();

    ResponseObject<?> getAllExamPaper(ListExamPaperRequest request);

    ResponseObject<?> getFile(String fileId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> getListMajorFacility();

    ResponseObject<?> createExamPaper(@Valid CreateExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid UpdateExamPaperRequest request);

    ResponseObject<?> sendEmailPublicExamPaper(String examPaperId);

}
