package fplhn.udpm.examdistribution.core.teacher.mockexampaper.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_TEACHER_MOCK_EXAM_PAPER)
public class TMockExamPaperController {

    @GetMapping
    public String viewMockExamPaper() {
        return "teacher/manage-mock-exam-paper/mock-exam-paper";
    }

}
