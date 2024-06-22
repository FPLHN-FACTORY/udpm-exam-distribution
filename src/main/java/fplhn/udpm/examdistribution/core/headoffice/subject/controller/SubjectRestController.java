package fplhn.udpm.examdistribution.core.headoffice.subject.controller;

import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.CreateUpdateSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.SubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.service.SubjectService;
import fplhn.udpm.examdistribution.core.headoffice.subject.service.impl.CommonService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_SUBJECT)
@RequiredArgsConstructor
public class SubjectRestController {

    private final SubjectService subjectService;

    private final CommonService commonService;

    @GetMapping
    public ResponseEntity<?> getAll(SubjectRequest request) {
        return Helper.createResponseEntity(subjectService.getAllSubject(request));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUpdateSubjectRequest request) {
        return Helper.createResponseEntity(subjectService.createSubject(request));
    }

    @PutMapping("/{subjectId}")
    public ResponseEntity<?> update(@PathVariable String subjectId, @RequestBody CreateUpdateSubjectRequest request) {
        return Helper.createResponseEntity(subjectService.updateSubject(subjectId, request));
    }

    @PutMapping("/{subjectId}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String subjectId) {
        return Helper.createResponseEntity(subjectService.changeSubjectStatus(subjectId));
    }

    @GetMapping("/{subjectId}")
    public ResponseEntity<?> getById(@PathVariable String subjectId) {
        return Helper.createResponseEntity(subjectService.getSubjectById(subjectId));
    }

    @GetMapping("/department")
    public ResponseEntity<?> getAllDepartment() {
        return Helper.createResponseEntity(commonService.getAllDepartmentSubject());
    }

}
