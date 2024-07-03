package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_TEACHER_EXAM_SHIFT)
public class ExamShiftController {

    @GetMapping
    public String viewHome(Model model) {
        Shift currentShift = Shift.getCurrentShift();
        model.addAttribute("currentShift", currentShift);
        return "teacher/manage-exam-shift/exam-shift";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "teacher/manage-exam-shift-detail/exam-shift-detail";
    }

}
