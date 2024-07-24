package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.request.UEPEditFileRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface UpdateExamPaperService {

    ResponseEntity<?> convertPdfToDocx(MultipartFile file);

    ResponseObject<?> editFile(UEPEditFileRequest request);

    ResponseObject<?> getFile(String examPaperId);

}
