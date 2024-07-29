package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListStaffBySubjectIdRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.PublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.SharePermissionExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import jakarta.validation.Valid;

public interface UploadExamPaperService {

    ResponseObject<?> getListCurrentSubject();

    ResponseObject<?> getListStaff();

    ResponseObject<?> getAllExamPaper(ListExamPaperRequest request);

    ResponseObject<?> getFile(String fileId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> createExamPaper(@Valid CreateExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid UpdateExamPaperRequest request);

    ResponseObject<?> sendEmailPublicMockExamPaper(String examPaperId);

    ResponseObject<?> sendEmailPublicMockExamPaper(PublicMockExamPaperRequest request);

    ResponseObject<?> getListStaffBySubjectId(String subjectId, ListStaffBySubjectIdRequest request);

    ResponseObject<?> sharePermissionExamPaper(SharePermissionExamPaperRequest request);

}
