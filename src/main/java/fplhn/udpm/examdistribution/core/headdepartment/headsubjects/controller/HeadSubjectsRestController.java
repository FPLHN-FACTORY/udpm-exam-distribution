package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.controller;

import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.AssignOrUnassignSubjectForHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectSearchRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.ReassignHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.service.HeadSubjectsService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
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
        return createResponseEntity(headSubjectsService.getSubjectsByHeadSubject(headSubjectId, request));
    }

    @GetMapping("/{headSubjectId}/subjects/assign")
    public ResponseEntity<?> getSubjectsWithAssign(
            @PathVariable String headSubjectId,
            SubjectByHeadSubjectRequest request
    ) {
        return createResponseEntity(headSubjectsService.getSubjectsWithAssign(headSubjectId, request));
    }

    @PutMapping("/{headSubjectId}/subjects/assign")
    public ResponseEntity<?> assignSubjectForHeadSubject(
            @PathVariable String headSubjectId,
            @RequestBody AssignOrUnassignSubjectForHeadSubjectRequest request
    ) {
        return createResponseEntity(headSubjectsService.assignSubjectForHeadSubject(headSubjectId, request));
    }

    @DeleteMapping("/{headSubjectId}/subjects/assign")
    public ResponseEntity<?> unassignSubjectForHeadSubject(
            @PathVariable String headSubjectId,
            @RequestBody AssignOrUnassignSubjectForHeadSubjectRequest request
    ) {
        return createResponseEntity(headSubjectsService.unassignSubjectForHeadSubject(headSubjectId, request));
    }

    @PutMapping("/subjects/reassign")
    public ResponseEntity<?> reassignSubjectForAnotherHeadSubject(
            @RequestBody ReassignHeadSubjectRequest request
    ) {
        return createResponseEntity(headSubjectsService.reassignSubjectForAnotherHeadSubject(request));
    }

    @GetMapping("/staff/search")
    public ResponseEntity<?> searchStaff(HeadSubjectSearchRequest request) {
        return createResponseEntity(headSubjectsService.searchStaff(request));
    }

}
