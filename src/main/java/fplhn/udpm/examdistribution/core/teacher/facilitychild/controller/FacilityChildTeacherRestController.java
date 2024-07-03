package fplhn.udpm.examdistribution.core.teacher.facilitychild.controller;

import fplhn.udpm.examdistribution.core.teacher.facilitychild.service.FacilityChildTeacherService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_CAMPUS)
public class FacilityChildTeacherRestController {

    private final FacilityChildTeacherService facilityChildTeacherService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return Helper.createResponseEntity(facilityChildTeacherService
                .findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId));
    }

}
