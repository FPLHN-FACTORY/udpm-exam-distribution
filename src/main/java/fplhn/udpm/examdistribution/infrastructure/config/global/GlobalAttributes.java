package fplhn.udpm.examdistribution.infrastructure.config.global;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalAttributes {

    @Value("${exam.distribution.development}")
    private Boolean isDevelopment;

    @Value("${exam.distribution.version}")
    private String appVersion;

    @ModelAttribute("isDevelopment")
    public Boolean isDevelopment() {
        return isDevelopment;
    }

    @ModelAttribute("appVersion")
    public String appVersion() {
        return appVersion;
    }

}
