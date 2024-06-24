package fplhn.udpm.examdistribution.core.headoffice.semester.controller;

import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.CreateUpdateSemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.service.SemesterService;
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
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_OFFICE_SEMESTER)
public class SemesterRestController {

    private final SemesterService semesterService;

    @GetMapping
    public ResponseEntity<?> getAll(SemesterRequest request) {
        return Helper.createResponseEntity(semesterService.getAllSemester(request));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUpdateSemesterRequest createUpdateSemesterRequest) {
        return Helper.createResponseEntity(semesterService.createSemester(createUpdateSemesterRequest));
    }

    @PutMapping("/{semesterId}")
    public ResponseEntity<?> update(@PathVariable String semesterId, @RequestBody CreateUpdateSemesterRequest createUpdateSemesterRequest) {
        return Helper.createResponseEntity(semesterService.updateSemester(semesterId, createUpdateSemesterRequest));
    }

    @GetMapping("/{semesterId}")
    public ResponseEntity<?> getSemesterById(@PathVariable String semesterId) {
        return Helper.createResponseEntity(semesterService.getSemesterById(semesterId));
    }

    @PutMapping("/status/{semesterId}")
    public ResponseEntity<?> statusChangeSemester(@PathVariable String semesterId) {
        return Helper.createResponseEntity(semesterService.statusChangeSemester(semesterId));
    }

}
