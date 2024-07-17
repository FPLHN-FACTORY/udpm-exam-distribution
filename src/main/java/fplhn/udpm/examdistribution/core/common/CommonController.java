package fplhn.udpm.examdistribution.core.common;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping(MappingConstants.HEAD_OFFICE)
    public String viewHeadOffice() {
        return "redirect:/head-office/semesters";
    }

    @GetMapping(MappingConstants.HEAD_DEPARTMENT)
    public String viewHeadDepartment() {
        return "redirect:/head-department/join-room";
    }

    @GetMapping(MappingConstants.HEAD_SUBJECT)
    public String viewHeadSubject() {
        return "redirect:/head-subject/join-room";
    }

    @GetMapping(MappingConstants.TEACHER)
    public String viewTeacher() {
        return "redirect:/teacher/exam-shift";
    }

    @GetMapping(MappingConstants.STUDENT)
    public String viewStudent() {
        return "redirect:/student/home";
    }

}
