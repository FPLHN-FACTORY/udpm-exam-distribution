package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TClassSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TClassSubjectService;
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
public class TClassSubjectRestController {

    private final TClassSubjectService tClassSubjectService;

    @GetMapping
    public ResponseEntity<?> getClassSubjectIdByRequest(TClassSubjectRequest classSubjectTeacherRequest) {
        return Helper.createResponseEntity(tClassSubjectService
                .getClassSubjectIdByRequest(classSubjectTeacherRequest));
    }

}
