package fplhn.udpm.examdistribution.core.headdepartment.joinroom.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDFacilityChildService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_CAMPUS)
public class HDFacilityChildRestController {

    private final HDFacilityChildService hdFacilityChildService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return Helper.createResponseEntity(hdFacilityChildService
                .findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId));
    }

}
