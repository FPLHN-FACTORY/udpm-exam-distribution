package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_UPDATE_EXAM_PAPER)
@RequiredArgsConstructor
public class UpdateExamPaperController {

    @GetMapping
    public String viewUpdateExamPaper() {
        return "head-subject/update-exam-paper/update-exam-paper";
    }

}
