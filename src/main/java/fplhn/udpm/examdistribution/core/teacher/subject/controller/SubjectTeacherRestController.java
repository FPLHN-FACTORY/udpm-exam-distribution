package fplhn.udpm.examdistribution.core.teacher.subject.controller;

import fplhn.udpm.examdistribution.core.teacher.subject.service.SubjectTeacherService;
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
public class SubjectTeacherRestController {

    private final SubjectTeacherService subjectTeacherService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCode(String classSubjectCode) {
        return Helper.createResponseEntity(subjectTeacherService.findAllByClassSubjectCode(classSubjectCode));
    }

}
