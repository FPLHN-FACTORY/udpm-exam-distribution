package fplhn.udpm.examdistribution.core.teacher.classsubject.controller;

import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.ClassSubjectTeacherRequest;
import fplhn.udpm.examdistribution.core.teacher.classsubject.service.ClassSubjectTeacherService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_CLASS_SUBJECT)
public class ClassSubjectTeacherRestController {

    private final ClassSubjectTeacherService classSubjectTeacherService;

    @GetMapping
    public ResponseEntity<?> getClassSubjectIdByRequest(ClassSubjectTeacherRequest classSubjectTeacherRequest) {
        return Helper.createResponseEntity(classSubjectTeacherService
                .getClassSubjectIdByRequest(classSubjectTeacherRequest));
    }

}
