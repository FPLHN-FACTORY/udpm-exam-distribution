package fplhn.udpm.examdistribution.core.headsubject.examrule.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.ChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.CreateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UpdateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.examrule.service.ExamRuleService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_EXAM_RULE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExamRuleRestController {

    private final ExamRuleService examRuleService;

    @GetMapping("/exam-rule")
    public ResponseEntity<?> getAllExamRule(FindExamRuleRequest request) {
        return Helper.createResponseEntity(examRuleService.getAllExamRule(request));
    }

    @PostMapping("/exam-rule")
    public ResponseEntity<?> createExamRule(@ModelAttribute CreateUploadExamRuleRequest request) {
        return Helper.createResponseEntity(examRuleService.createExamRule(request));
    }

    @PutMapping("/exam-rule")
    public ResponseEntity<?> updateExamRule(@ModelAttribute UpdateUploadExamRuleRequest request) {
        return Helper.createResponseEntity(examRuleService.updateExamRule(request));
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> getListSubject(FindSubjectRequest request) {
        return Helper.createResponseEntity(examRuleService.getListSubject(request));
    }

    @PostMapping("/choose-exam-rule")
    public ResponseEntity<?> chooseExamRule(@RequestBody ChooseExamRuleRequest request) {
        return Helper.createResponseEntity(examRuleService.chooseExamRule(request));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        ResponseObject<?> responseObject = examRuleService.getFile(id);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            FileResponse fileResponse = (FileResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
