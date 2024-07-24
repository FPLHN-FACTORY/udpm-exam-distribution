package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSFacilityChildService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_CAMPUS)
public class HSFacilityChildRestController {

    private final HSFacilityChildService hsFacilityChildService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return Helper.createResponseEntity(hsFacilityChildService
                .findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId));
    }

}
