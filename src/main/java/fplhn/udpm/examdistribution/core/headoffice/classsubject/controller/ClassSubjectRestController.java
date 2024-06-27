package fplhn.udpm.examdistribution.core.headoffice.classsubject.controller;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.CreateUpdateClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.service.ClassSubjectService;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.service.impl.ClassSubjectCommonService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_CLASS_SUBJECT)
@RequiredArgsConstructor
public class ClassSubjectRestController {

    private final ClassSubjectService classSubjectService;

    private final ClassSubjectCommonService commonService;

    @GetMapping
    public ResponseEntity<?> getAll(ClassSubjectRequest request) {
        return Helper.createResponseEntity(classSubjectService.getAllClassSubject(request));
    }

    @GetMapping("/get-all-by-keyword")
    public ResponseEntity<?> getAllClassSubjectByKeyword(ClassSubjectKeywordRequest request) {
        return Helper.createResponseEntity(classSubjectService.getAllClassSubjectByKeyword(request));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUpdateClassSubjectRequest request) {
        return Helper.createResponseEntity(classSubjectService.createClassSubject(request));
    }

    @PutMapping("/{classSubjectId}")
    public ResponseEntity<?> update(@PathVariable String classSubjectId, @RequestBody CreateUpdateClassSubjectRequest request) {
        return Helper.createResponseEntity(classSubjectService.updateClassSubject(classSubjectId, request));
    }

    @PutMapping("/{classSubjectId}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String classSubjectId) {
        return Helper.createResponseEntity(classSubjectService.changeClassSubjectStatus(classSubjectId));
    }

    @GetMapping("/{classSubjectId}")
    public ResponseEntity<?> getById(@PathVariable String classSubjectId) {
        return Helper.createResponseEntity(classSubjectService.getClassSubjectById(classSubjectId));
    }

    @GetMapping("/block")
    public ResponseEntity<?> getAllBlock() {
        return Helper.createResponseEntity(commonService.getAllBlock());
    }

    @GetMapping("/block-by-year")
    public ResponseEntity<?> getAllBlockByYear(@RequestParam Integer year) {
        return Helper.createResponseEntity(commonService.getAllBlockByYear(year));
    }

    @GetMapping("/facility-child")
    public ResponseEntity<?> getAllFacilityChild() {
        return Helper.createResponseEntity(commonService.getAllFacilityChild());
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getAllStaff() {
        return Helper.createResponseEntity(commonService.getAllStaff());
    }

    @GetMapping("/staff/{staffCode}")
    public ResponseEntity<?> findStaffByCode(@PathVariable String staffCode) {
        return Helper.createResponseEntity(commonService.findStaffByCode(staffCode));
    }

    @GetMapping("/subject")
    public ResponseEntity<?> getAllSubject() {
        return Helper.createResponseEntity(commonService.getAllSubject());
    }

    @GetMapping("/subject/{subjectCode}")
    public ResponseEntity<?> findSubjectByCode(@PathVariable String subjectCode) {
        return Helper.createResponseEntity(commonService.findSubjectByCode(subjectCode));
    }

}
