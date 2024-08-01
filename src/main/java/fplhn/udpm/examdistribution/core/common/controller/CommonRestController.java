package fplhn.udpm.examdistribution.core.common.controller;

import fplhn.udpm.examdistribution.core.common.service.CommonServiceHelper;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_COMMON)
@RequiredArgsConstructor
public class CommonRestController {

    private final CommonServiceHelper commonServiceHelper;

    @GetMapping("/semester")
    public ResponseEntity<?> getSemester() {
        return Helper.createResponseEntity(commonServiceHelper.getSemesterInfo());
    }

}
