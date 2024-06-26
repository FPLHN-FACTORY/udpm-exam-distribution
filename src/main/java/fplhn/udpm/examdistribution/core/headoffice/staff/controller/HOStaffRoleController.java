package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_STAFF)
public class HOStaffRoleController {

    @GetMapping("/{id}")
    public String viewStaffs() {
        return "head-office/manage-staff/staff-role";
    }

}
