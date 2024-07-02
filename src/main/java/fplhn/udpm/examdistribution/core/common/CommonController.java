package fplhn.udpm.examdistribution.core.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/head-office")
    public String viewHeadOffice() {
        return "redirect:/head-office/semesters";
    }

    @GetMapping("/head-department")
    public String viewHeadDepartment() {
        return "redirect:/head-department/manage-head-of-subjects";
    }

}
