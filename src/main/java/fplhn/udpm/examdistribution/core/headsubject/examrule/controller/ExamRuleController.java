package fplhn.udpm.examdistribution.core.headsubject.examrule.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_EXAM_RULE)
public class ExamRuleController {

    @GetMapping
    public String viewExamRule() {
        return "/head-subject/exam-rule/exam-rule";
    }

}
