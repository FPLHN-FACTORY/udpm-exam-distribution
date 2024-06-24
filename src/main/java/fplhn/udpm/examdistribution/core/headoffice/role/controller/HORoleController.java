package fplhn.udpm.examdistribution.core.headoffice.role.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_ROLE)
public class HORoleController {

    @GetMapping
    public String viewRole() {
        return "head-office/manage-role/role";
    }

}
