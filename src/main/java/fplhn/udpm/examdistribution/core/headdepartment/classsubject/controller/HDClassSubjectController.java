package fplhn.udpm.examdistribution.core.headdepartment.classsubject.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_CLASS_SUBJECT)
public class HDClassSubjectController {

    @GetMapping
    public String viewClassSubjects() {
        return "head-department/manage-class-subject/class-subject";
    }

}
