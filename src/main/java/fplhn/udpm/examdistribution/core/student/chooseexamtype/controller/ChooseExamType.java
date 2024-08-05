package fplhn.udpm.examdistribution.core.student.chooseexamtype.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_STUDENT_CHOOSE_EXAM_TYPE)
public class ChooseExamType {

    @GetMapping
    public String chooseExamType() {
        return "student/choose-exam-type/choose-exam-type";
    }

}
