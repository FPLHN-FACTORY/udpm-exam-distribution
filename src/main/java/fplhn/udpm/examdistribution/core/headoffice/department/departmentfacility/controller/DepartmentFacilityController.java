package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_DEPARTMENT_FACILITY)
public class DepartmentFacilityController {

    @GetMapping("/{id}")
    public String viewDepartmentFacility(@PathVariable String id) {
        return "head-office/manage-department/departments-facility";
    }

}
