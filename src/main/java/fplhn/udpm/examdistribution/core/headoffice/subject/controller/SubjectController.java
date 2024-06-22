package fplhn.udpm.examdistribution.core.headoffice.subject.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_SUBJECT)
public class SubjectController {

    @GetMapping
    public String viewSubjects() {
        return "head-office/manage-subject/subject";
    }

}
