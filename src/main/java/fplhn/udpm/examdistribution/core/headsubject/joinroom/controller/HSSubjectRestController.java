package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDSubjectService;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSSubjectService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_SUBJECT)
public class HSSubjectRestController {

    private final HSSubjectService hsSubjectService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCode(String classSubjectCode) {
        return Helper.createResponseEntity(hsSubjectService.findAllByClassSubjectCode(classSubjectCode));
    }

}
