package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.controller;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPGetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.service.ChooseExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_CHOOSE_EXAM_PAPER)
@RequiredArgsConstructor
public class ChooseExamPaperRestController {

    private final ChooseExamPaperService chooseExamPaperService;

    @GetMapping("/subject/{semesterId}")
    public ResponseEntity<?> getListSubject(@PathVariable String semesterId) {
        return Helper.createResponseEntity(chooseExamPaperService.getListSubject(semesterId));
    }

    @GetMapping("/current-subject")
    public ResponseEntity<?> getListCurrentSubject() {
        return Helper.createResponseEntity(chooseExamPaperService.getListCurrentSubject());
    }

    @GetMapping("/semester")
    public ResponseEntity<?> getListSemester() {
        return Helper.createResponseEntity(chooseExamPaperService.getListSemester());
    }

    @GetMapping("/block/{semesterId}")
    public ResponseEntity<?> getListBlock(@PathVariable String semesterId) {
        return Helper.createResponseEntity(chooseExamPaperService.getListBlock(semesterId));
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getListStaff() {
        return Helper.createResponseEntity(chooseExamPaperService.getListStaff());
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(CEPGetFileRequest request) throws IOException {
        return Helper.createResponseEntity(chooseExamPaperService.getFile(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExamPaper(@PathVariable String id) {
        return Helper.createResponseEntity(chooseExamPaperService.deleteExamPaper(id));
    }

    @GetMapping("/exam-paper")
    public ResponseEntity<?> getListExamPaper(CEPListExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.getAllExamPaper(request));
    }

    @PostMapping("/exam-paper")
    public ResponseEntity<?> createExamPaper(@ModelAttribute CEPCreateExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.createExamPaper(request));
    }

    @PutMapping("/exam-paper")
    public ResponseEntity<?> updateExamPaper(@ModelAttribute CEPUpdateExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.updateExamPaper(request));
    }

    @PostMapping("/send-email-public-exam-paper/{examPaperId}")
    public ResponseEntity<?> sendEmailPublicExamPaper(@PathVariable String examPaperId) {
        return Helper.createResponseEntity(chooseExamPaperService.sendEmailPublicExamPaper(examPaperId));
    }

    @PostMapping("/choose/{examPaperId}")
    public ResponseEntity<?> chooseExamPaper(@PathVariable String examPaperId) {
        return Helper.createResponseEntity(chooseExamPaperService.chooseExamPaper(examPaperId));
    }

    @PostMapping("/pdf-to-docx")
    public ResponseEntity<?> convertPdfToDocx(@RequestParam("file") MultipartFile file) {
        return chooseExamPaperService.convertPdfToDocx(file);
    }

    @GetMapping("/resources")
    public ResponseEntity<?> getListResource(CEPListResourceExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.getListResource(request));
    }

    @PostMapping("/resources")
    public ResponseEntity<?> createResource(@RequestBody CEPCreateResourceExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.createResource(request));
    }

    @PutMapping("/resources")
    public ResponseEntity<?> updateResource(@RequestBody CEPUpdateResourceExamPaperRequest request) {
        return Helper.createResponseEntity(chooseExamPaperService.updateResource(request));
    }

    @GetMapping("/detail-resource/{resourceExamPaperId}")
    public ResponseEntity<?> detailResource(@PathVariable String resourceExamPaperId) {
        return Helper.createResponseEntity(chooseExamPaperService.detailResource(resourceExamPaperId));
    }

}
