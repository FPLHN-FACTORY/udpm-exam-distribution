package fplhn.udpm.examdistribution.core.student.home.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_STUDENT_HOME)
public class HomeController {

    @GetMapping
    public String viewHome() {
        return "student/home/student-home";
    }

}
