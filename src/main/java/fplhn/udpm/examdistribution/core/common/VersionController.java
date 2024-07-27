package fplhn.udpm.examdistribution.core.common;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/version")
public class VersionController {

    @GetMapping
    public String getVersion() {
        return "version";
    }

}
