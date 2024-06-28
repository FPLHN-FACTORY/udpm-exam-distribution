package fplhn.udpm.examdistribution.core.headoffice.staffsubject.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_STAFF_SUBJECT)
public class StaffSubjectController {

    @GetMapping
    public String viewStaffSubjects() {
        return "head-office/manage-class-subject/class-subject";
    }

}
