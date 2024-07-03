package fplhn.udpm.examdistribution.core.common;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping(MappingConstants.HEAD_OFFICE)
    public String viewHeadOffice() {
        return "redirect:/head-office/semesters";
    }

    @GetMapping(MappingConstants.HEAD_DEPARTMENT)
    public String viewHeadDepartment() {
        return "redirect:/head-department/manage-head-of-subjects";
    }

    @GetMapping(MappingConstants.HEAD_SUBJECT)
    public String viewHeadSubject() {
        return "redirect:/head-subject/join-room";
    }

}
