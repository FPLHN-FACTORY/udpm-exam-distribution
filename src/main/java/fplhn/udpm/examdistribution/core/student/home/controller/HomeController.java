package fplhn.udpm.examdistribution.core.student.home.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_STUDENT_HOME)
public class HomeController {

    @GetMapping
    public String viewHome() {
        return "student/home/student-home";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "student/exam-shift/exam-shift-join";
    }

}
