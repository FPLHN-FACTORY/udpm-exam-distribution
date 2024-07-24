package fplhn.udpm.examdistribution.core.headsubject.createexampaper.controller;

import fplhn.udpm.examdistribution.core.headsubject.createexampaper.model.request.CREPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.service.CreateExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_CREATE_EXAM_PAPER)
@RequiredArgsConstructor
public class CreateExamPaperRestController {

    private final CreateExamPaperService createExamPaperService;

    @PostMapping("/pdf-to-docx")
    public ResponseEntity<?> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        return createExamPaperService.convertPdfToDocx(file);
    }

    @GetMapping("/major-facility")
    public ResponseEntity<?> getListMajorFacility() {
        return Helper.createResponseEntity(createExamPaperService.getListMajorFacility());
    }

    @GetMapping("/subject")
    public ResponseEntity<?> getListSubject() {
        return Helper.createResponseEntity(createExamPaperService.getListSubject());
    }

    @PostMapping("/exam-paper")
    public ResponseEntity<?> createExamPaper(@ModelAttribute CREPCreateExamPaperRequest request) {
        return Helper.createResponseEntity(createExamPaperService.createExamPaper(request));
    }

}
