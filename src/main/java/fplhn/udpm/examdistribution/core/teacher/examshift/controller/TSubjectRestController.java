package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.teacher.examshift.service.TSubjectService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_SUBJECT)
public class TSubjectRestController {

    private final TSubjectService tSubjectService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCode(String classSubjectCode) {
        return Helper.createResponseEntity(tSubjectService.findAllByClassSubjectCode(classSubjectCode));
    }

}
