package fplhn.udpm.examdistribution.core.teacher.examfile.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_TEACHER_EXAM_FILE)
public class TExamFileController {

    @GetMapping
    public String viewExamFile() {
        return "teacher/manage-exam-file/exam-file";
    }

}
