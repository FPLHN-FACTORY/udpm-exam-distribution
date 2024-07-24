package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.controller;

import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.request.UEPEditFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.service.UpdateExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
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
        return Helper.createResponseEntity(updateExamPaperService.getFile(examPaperId));
    }

}
