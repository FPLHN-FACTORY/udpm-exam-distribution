package fplhn.udpm.examdistribution.core.headsubject.createexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.model.request.CREPCreateExamPaperRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CreateExamPaperService {

    ResponseEntity<?> convertPdfToDocx(MultipartFile file);

    ResponseObject<?> getListSubject();

    ResponseObject<?> getListMajorFacility();

    ResponseObject<?> createExamPaper(@Valid CREPCreateExamPaperRequest request);
}
