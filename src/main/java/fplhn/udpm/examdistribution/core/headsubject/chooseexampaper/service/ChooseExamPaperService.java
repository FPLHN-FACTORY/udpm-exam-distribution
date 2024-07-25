package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateExamPaperRequest;
import jakarta.validation.Valid;

public interface ChooseExamPaperService {

    ResponseObject<?> getListSubject(String semesterId);

    ResponseObject<?> getListCurrentSubject();

    ResponseObject<?> getListSemester();

    ResponseObject<?> getListBlock(String semesterId);

    ResponseObject<?> getListStaff();

    ResponseObject<?> getAllExamPaper(CEPListExamPaperRequest request);

    ResponseObject<?> getFile(String fileId);

    ResponseObject<?> deleteExamPaper(String examPaperId);

    ResponseObject<?> createExamPaper(@Valid CEPCreateExamPaperRequest request);

    ResponseObject<?> updateExamPaper(@Valid CEPUpdateExamPaperRequest request);

    ResponseObject<?> sendEmailPublicExamPaper(String examPaperId);

    ResponseObject<?> chooseExamPaper(String examPaperId);

}
