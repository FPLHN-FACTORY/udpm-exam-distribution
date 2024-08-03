package fplhn.udpm.examdistribution.core.headoffice.examrule.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_OFFICE_STAFF_EXAM_RULE)
public class HOExamRuleController {

    @GetMapping
    public String viewExamRule() {
        return "head-office/manage-exam-rule/exam-rule";
    }

}
