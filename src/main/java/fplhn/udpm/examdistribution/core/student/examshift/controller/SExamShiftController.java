package fplhn.udpm.examdistribution.core.student.examshift.controller;

import fplhn.udpm.examdistribution.core.student.examshift.service.SExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.REDIRECT_STUDENT_EXAM_SHIFT)
public class SExamShiftController {

    private final SExamShiftService studentExamShiftService;

    @GetMapping
    public String viewHome() {
        return "student/exam-shift/exam-shift";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        if (!studentExamShiftService.findStudentInExamShift(examShiftCode)) {
            return "error/404";
        }
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "student/exam-shift-detail/exam-shift-detail";
    }

}
