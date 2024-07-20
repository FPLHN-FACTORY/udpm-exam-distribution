package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_CHOOSE_EXAM_PAPER)
@RequiredArgsConstructor
public class ChooseExamPaperController {

    @GetMapping
    public String viewChooseExamPaper() {
        return "head-subject/choose-exam-paper/choose-exam-paper";
    }

}
