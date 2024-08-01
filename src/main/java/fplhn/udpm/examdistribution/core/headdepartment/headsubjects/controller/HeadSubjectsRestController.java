package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.controller;

import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service.HeadSubjectsService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static fplhn.udpm.examdistribution.utils.Helper.createResponseEntity;

@RestController
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_HEAD_SUBJECTS)
@RequiredArgsConstructor
public class HeadSubjectsRestController {

    private final HeadSubjectsService headSubjectsService;

    @GetMapping
    public ResponseEntity<?> getHeadSubjects(HeadSubjectRequest request) {
        return createResponseEntity(headSubjectsService.getAllHeadSubjects(request));
    }

    @GetMapping("/{headSubjectId}/subjects")
    public ResponseEntity<?> getSubjectsByHeadSubject(
            @PathVariable String headSubjectId,
            SubjectByHeadSubjectRequest request
    ) {
        request.setHeadSubjectId(headSubjectId);
        return createResponseEntity(headSubjectsService.getSubjectsByHeadSubject(request));
    }

    @GetMapping("/{headSubjectId}/subjects/assign")
    public ResponseEntity<?> getSubjectsWithAssign(
            @PathVariable String headSubjectId,
            SubjectByHeadSubjectRequest request
    ) {
        request.setHeadSubjectId(headSubjectId);
        return createResponseEntity(headSubjectsService.getSubjectsWithAssign(request));
    }

    @PostMapping("/{headSubjectId}/subjects/assign")
    public ResponseEntity<?> assignSubjectForHeadSubject(
            @PathVariable String headSubjectId,
            @RequestBody AssignSubjectForHeadSubjectRequest request
    ) {
        return createResponseEntity(headSubjectsService.assignSubjectForHeadSubject(headSubjectId, request));
    }

}
