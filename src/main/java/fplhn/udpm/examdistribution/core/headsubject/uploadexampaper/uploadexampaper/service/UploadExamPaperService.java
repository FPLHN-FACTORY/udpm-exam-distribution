package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPListStaffBySubjectIdRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPPublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPSharePermissionExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UEPUpdateExamPaperRequest;
import jakarta.validation.Valid;

public interface UploadExamPaperService {

    ResponseObject<?> getListCurrentSubject();

    ResponseObject<?> getListStaff();

    ResponseObject<?> getAllExamPaper(UEPListExamPaperRequest request);

    ResponseObject<?> getFile(String fileId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> createExamPaper(@Valid UEPCreateExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid UEPUpdateExamPaperRequest request);

    ResponseObject<?> sendEmailPublicMockExamPaper(String examPaperId);

    ResponseObject<?> sendEmailPublicMockExamPaper(UEPPublicMockExamPaperRequest request);

    ResponseObject<?> getListStaffBySubjectId(String subjectId, UEPListStaffBySubjectIdRequest request);

    ResponseObject<?> sharePermissionExamPaper(UEPSharePermissionExamPaperRequest request);

    ResponseObject<?> getListResource(UEPListResourceExamPaperRequest request);

}
