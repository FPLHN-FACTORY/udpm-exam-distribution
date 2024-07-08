package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.AddExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER)
@RequiredArgsConstructor
public class UploadExamPaperRestController {

    private final UploadExamPaperService uploadExamPaperService;

    @GetMapping("/subject")
    public ResponseEntity<?> getListSubject() {
        return Helper.createResponseEntity(uploadExamPaperService.getListSubject());
    }

    @GetMapping("/major-facility")
    public ResponseEntity<?> getListMajorFacility() {
        return Helper.createResponseEntity(uploadExamPaperService.getListMajorFacility());
    }

    @GetMapping("/exam-paper")
    public ResponseEntity<?> getListExamPaper(ListExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.getAllExamPaper(request));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = uploadExamPaperService.getFile(fileId);
        Resource resource = (Resource) responseObject.getData();
        String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExamPaper(@PathVariable String id) {
        return Helper.createResponseEntity(uploadExamPaperService.deleteExamPaper(id));
    }

    @PostMapping("/exam-paper")
    public ResponseEntity<?> addExamPaper(@ModelAttribute AddExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.addExamPaper(request));
    }

    @PutMapping("/exam-paper")
    public ResponseEntity<?> updateExamPaper(@ModelAttribute UpdateExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.updateExamPaper(request));
    }

}
