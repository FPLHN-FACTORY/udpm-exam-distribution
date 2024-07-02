package fplhn.udpm.examdistribution.core.authentication.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    private final HttpSession httpSession;

    @GetMapping
    public String viewAuthentication(Model model) {
        if(httpSession.getAttribute(SessionConstant.ERROR_LOGIN) != null){
            model.addAttribute("error", httpSession.getAttribute(SessionConstant.ERROR_LOGIN));
        }
        return "authentication/authentication";
    }

}
