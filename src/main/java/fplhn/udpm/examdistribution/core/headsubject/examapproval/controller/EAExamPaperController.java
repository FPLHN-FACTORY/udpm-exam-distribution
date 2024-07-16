package fplhn.udpm.examdistribution.core.headsubject.examapproval.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL)
public class EAExamPaperController {

    @GetMapping
    public String viewExamApproval() {
        return "head-subject/exam-approval/exam-approval";
    }

}
