package fplhn.udpm.examdistribution.core.headdepartment.examshift.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_EXAM_SHIFT)
public class HDManageExamShiftController {

    @GetMapping
    public String viewExamShift() {
        return "head-department/manage-exam-shift/manage-exam-shift";
    }

}
