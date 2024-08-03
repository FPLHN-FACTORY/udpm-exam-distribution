package fplhn.udpm.examdistribution.core.headsubject.subjectrule.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_SUBJECT_RULE)
public class SubjectRuleController {

    @GetMapping
    public String viewExamRule() {
        return "head-subject/subject-rule/subject-rule";
    }

}
