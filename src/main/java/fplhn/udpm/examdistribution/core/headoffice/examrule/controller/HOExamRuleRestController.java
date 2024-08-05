package fplhn.udpm.examdistribution.core.headoffice.examrule.controller;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOCreateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOUpdateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.service.HOExamRuleService;
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
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF_EXAM_RULE)
@RequiredArgsConstructor
@CrossOrigin("*")
public class HOExamRuleRestController {

    private final HOExamRuleService HOExamRuleService;

    @GetMapping("/exam-rule")
    public ResponseEntity<?> getAllExamRule(HOFindExamRuleRequest request) {
        return Helper.createResponseEntity(HOExamRuleService.getAllExamRule(request));
    }

    @PostMapping("/exam-rule")
    public ResponseEntity<?> createExamRule(@ModelAttribute HOCreateUploadExamRuleRequest request) {
        return Helper.createResponseEntity(HOExamRuleService.createExamRule(request));
    }

    @PutMapping("/exam-rule")
    public ResponseEntity<?> updateExamRule(@ModelAttribute HOUpdateUploadExamRuleRequest request) {
        return Helper.createResponseEntity(HOExamRuleService.updateExamRule(request));
    }

    @GetMapping("/subjects")
    public ResponseEntity<?> getListSubject(HOFindSubjectRequest request) {
        return Helper.createResponseEntity(HOExamRuleService.getListSubject(request));
    }

    @PostMapping("/choose-exam-rule")
    public ResponseEntity<?> chooseExamRule(@RequestBody HOChooseExamRuleRequest request) {
        return Helper.createResponseEntity(HOExamRuleService.chooseExamRule(request));
    }

    @GetMapping("/file/{id}")
    public ResponseEntity<?> getFile(@PathVariable String id) {
        ResponseObject<?> responseObject = HOExamRuleService.getFile(id);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            FileResponse HOFileResponse = (FileResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + HOFileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(HOFileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
