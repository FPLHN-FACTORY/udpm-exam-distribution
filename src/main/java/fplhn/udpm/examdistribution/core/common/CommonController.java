package fplhn.udpm.examdistribution.core.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CommonController {

    @GetMapping("/head-office")
    public String viewHeadOffice() {
        return "redirect:/head-office/block";
    }

}
