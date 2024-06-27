package fplhn.udpm.examdistribution.core.headoffice.classsubject.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_CLASS_SUBJECT)
public class ClassSubjectController {

    @GetMapping
    public String viewClassSubjects() {
        return "head-office/manage-class-subject/class-subject";
    }

}
