package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPGetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateResourceExamPaperRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface ChooseExamPaperService {

    ResponseObject<?> getListSubject(String semesterId);

    ResponseObject<?> getListCurrentSubject();

    ResponseObject<?> getListSemester();

    ResponseObject<?> getListBlock(String semesterId);

    ResponseObject<?> getListStaff();

    ResponseObject<?> getAllExamPaper(CEPListExamPaperRequest request);

    ResponseObject<?> getFile(CEPGetFileRequest request);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> createExamPaper(@Valid CEPCreateExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid CEPUpdateExamPaperRequest request);

    ResponseObject<?> sendEmailPublicExamPaper(String examPaperId);

    ResponseObject<?> chooseExamPaper(String examPaperId);

    ResponseEntity<?> convertPdfToDocx(MultipartFile file);

    ResponseObject<?> getListResource(CEPListResourceExamPaperRequest request);

    ResponseObject<?> createResource(@Valid CEPCreateResourceExamPaperRequest request);

    ResponseObject<?> updateResource(@Valid CEPUpdateResourceExamPaperRequest request);

    ResponseObject<?> detailResource(String resourceExamPaperId);

}
