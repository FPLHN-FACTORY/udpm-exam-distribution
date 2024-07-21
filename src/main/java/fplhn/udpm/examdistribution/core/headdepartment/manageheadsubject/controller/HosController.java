package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_HOS)
public class HosController {

    @GetMapping
    public String viewHos() {
        return "head-department/manage-head-of-subject/manage-hos";
    }

}
