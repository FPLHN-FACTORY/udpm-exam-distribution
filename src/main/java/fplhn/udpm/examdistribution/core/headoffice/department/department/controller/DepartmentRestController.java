package fplhn.udpm.examdistribution.core.headoffice.department.department.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_DEPARTMENT)
public class DepartmentRestController {

    @GetMapping
    public String viewDepartments() {
        return "head-office/manage-department/departments";
    }

}
