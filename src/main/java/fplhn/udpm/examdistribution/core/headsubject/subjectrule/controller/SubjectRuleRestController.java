package fplhn.udpm.examdistribution.core.headsubject.subjectrule.controller;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRUpdateExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.service.SRSubjectRuleService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_SUBJECT_RULE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class SubjectRuleRestController {

    private final SRSubjectRuleService SRSubjectRuleService;

    @GetMapping("/subjects")
    public ResponseEntity<?> getListSubject(SRFindSubjectRequest request) {
        return Helper.createResponseEntity(SRSubjectRuleService.getListSubject(request));
    }

    @GetMapping("/exam-rules")
    public ResponseEntity<?> getListExamRule(SRFindSubjectRuleRequest request) {
        return Helper.createResponseEntity(SRSubjectRuleService.getListExamRule(request));
    }

    @PutMapping("/exam-rule")
    public ResponseEntity<?> chooseExamRule(@RequestBody SRChooseExamRuleRequest request) {
        return Helper.createResponseEntity(SRSubjectRuleService.chooseExamRule(request));
    }

    @GetMapping("/exam-time")
    public ResponseEntity<?> getExamTime(SRExamTimeRequest request) {
        return Helper.createResponseEntity(SRSubjectRuleService.getExamTime(request));
    }

    @PutMapping("/exam-time")
    public ResponseEntity<?> updateExamTime(@RequestBody SRUpdateExamTimeRequest request) {
        return Helper.createResponseEntity(SRSubjectRuleService.updateExamTime(request));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        ResponseObject<?> responseObject = SRSubjectRuleService.getFile(id);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            FileResponse SRFileResponse = (FileResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + SRFileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(SRFileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
