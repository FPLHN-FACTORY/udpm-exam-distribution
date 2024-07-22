package fplhn.udpm.examdistribution.core.headsubject.createexampaper.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_CREATE_EXAM_PAPER)
public class CreateExamPaperController {

    @GetMapping
    public String viewCreateExamPaper() {
        return "head-subject/createexampaper/create-exam-paper";
    }

}
