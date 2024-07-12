package fplhn.udpm.examdistribution.core.teacher.mockexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TSubjectMockExamRequest;

import java.io.IOException;

public interface TMockExamPaperService {

    ResponseObject<?> getAllSubject(TSubjectMockExamRequest request);

    ResponseObject<?> getMockExams(TMockExamPaperRequest request);

    ResponseObject<?> getSemesters();

    ResponseObject<?> getFile(String idMockExamPaper) throws IOException;

    ResponseObject<?> downLoad(String fileId);

}
