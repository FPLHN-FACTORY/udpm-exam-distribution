package fplhn.udpm.examdistribution.core.headsubject.examrule.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.service.ExamRuleService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Base64;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_EXAM_RULE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class ExamRuleRestController {

    private final ExamRuleService examRuleService;

    @GetMapping("/subject/{departmentFacilityId}")
    public ResponseEntity<?> getAllSubject(@PathVariable String departmentFacilityId, FindSubjectRequest request) {
        return Helper.createResponseEntity(examRuleService.getAllSubject(departmentFacilityId, request));
    }

    @PostMapping("/upload/{subjectId}")
    public ResponseEntity<?> uploadExamRule(@PathVariable String subjectId, @ModelAttribute UploadExamRuleRequest request) {
        return Helper.createResponseEntity(examRuleService.uploadExamRule(subjectId, request));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(GetFileRequest request) throws IOException {
        ResponseObject<?> responseObject = examRuleService.getFile(request);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            Resource resource = (Resource) responseObject.getData();
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        }
        return Helper.createResponseEntity(responseObject);
    }

}
