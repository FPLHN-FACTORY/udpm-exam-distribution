package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.controller;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER)
@RequiredArgsConstructor
public class UploadExamPaperRestController {

    private final UploadExamPaperService uploadExamPaperService;

    @GetMapping("/subject")
    public ResponseEntity<?> getListSubject() {
        return Helper.createResponseEntity(uploadExamPaperService.getListSubject());
    }

    @GetMapping("/exam-paper")
    public ResponseEntity<?> getListExamPaper() {
        return Helper.createResponseEntity(uploadExamPaperService.getAllExamPaper());
    }

}
