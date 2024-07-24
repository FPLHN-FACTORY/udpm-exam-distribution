package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.controller;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateExamPaperRequest;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/major-facility")
    public ResponseEntity<?> getListMajorFacility() {
        return Helper.createResponseEntity(chooseExamPaperService.getListMajorFacility());
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getListStaff() {
        return Helper.createResponseEntity(chooseExamPaperService.getListStaff());
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        return Helper.createResponseEntity(chooseExamPaperService.getFile(fileId));
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

}
