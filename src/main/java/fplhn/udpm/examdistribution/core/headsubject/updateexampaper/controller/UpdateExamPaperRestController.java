package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.request.UEPEditFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.response.UEPFileResponse;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.service.UpdateExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_UPDATE_EXAM_PAPER)
@RequiredArgsConstructor
public class UpdateExamPaperRestController {

    private final UpdateExamPaperService updateExamPaperService;

    @PostMapping("/pdf-to-docx")
    public ResponseEntity<?> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        return updateExamPaperService.convertPdfToDocx(file);
    }

    @PutMapping("/edit-file")
    public ResponseEntity<?> editFile(@ModelAttribute UEPEditFileRequest request) {
        return Helper.createResponseEntity(updateExamPaperService.editFile(request));
    }

    @GetMapping("/file/{examPaperId}")
    public ResponseEntity<?> getFile(@PathVariable String examPaperId) {
        ResponseObject<?> responseObject = updateExamPaperService.getFile(examPaperId);
        if (responseObject.getStatus().equals(HttpStatus.BAD_REQUEST) || responseObject.getStatus().equals(HttpStatus.NOT_FOUND)) {
            return Helper.createResponseEntity(responseObject);
        }

        UEPFileResponse fileResponse = (UEPFileResponse) responseObject.getData();
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(fileResponse.getData());
    }

}
