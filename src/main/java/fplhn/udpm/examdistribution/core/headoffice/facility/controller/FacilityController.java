package fplhn.udpm.examdistribution.core.headoffice.facility.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_FACILITY)
public class FacilityController {

    @GetMapping
    public String viewFacilities() {
        return "head-office/manage-facility/facility";
    }

}
