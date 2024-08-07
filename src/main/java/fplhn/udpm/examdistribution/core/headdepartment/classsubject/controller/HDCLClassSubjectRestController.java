package fplhn.udpm.examdistribution.core.headdepartment.classsubject.controller;

import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.BlockRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.CreateUpdateClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.HDCLClassSubjectService;
import fplhn.udpm.examdistribution.core.headdepartment.classsubject.service.impl.HDClassSubjectCommonService;
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
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT)
@RequiredArgsConstructor
public class HDCLClassSubjectRestController {

    private final HDCLClassSubjectService HDCLClassSubjectService;

    private final HDClassSubjectCommonService commonService;

    @GetMapping
    public ResponseEntity<?> getAll(ClassSubjectRequest request) {
        return Helper.createResponseEntity(HDCLClassSubjectService.getAllClassSubject(request));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUpdateClassSubjectRequest request) {
        return Helper.createResponseEntity(HDCLClassSubjectService.createClassSubject(request));
    }

    @PutMapping("/{classSubjectId}")
    public ResponseEntity<?> update(@PathVariable String classSubjectId, @RequestBody CreateUpdateClassSubjectRequest request) {
        return Helper.createResponseEntity(HDCLClassSubjectService.updateClassSubject(classSubjectId, request));
    }

    @PutMapping("/{classSubjectId}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String classSubjectId) {
        return Helper.createResponseEntity(HDCLClassSubjectService.changeClassSubjectStatus(classSubjectId));
    }

    @GetMapping("/{classSubjectId}")
    public ResponseEntity<?> getById(@PathVariable String classSubjectId) {
        return Helper.createResponseEntity(HDCLClassSubjectService.getClassSubjectById(classSubjectId));
    }

    @GetMapping("/block-by-year")
    public ResponseEntity<?> getAllBlockByYear(BlockRequest request) {
        return Helper.createResponseEntity(commonService.getAllBlockByYear(request));
    }

    @GetMapping("/semester-by-name-year")
    public ResponseEntity<?> findByNameAndYear(SemesterRequest request) {
        return Helper.createResponseEntity(commonService.findBySemesterByNameAndYear(request));
    }

    @GetMapping("/facility-child")
    public ResponseEntity<?> getAllFacilityChild() {
        return Helper.createResponseEntity(commonService.getAllFacilityChild());
    }

}
