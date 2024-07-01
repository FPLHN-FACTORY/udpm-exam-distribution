package fplhn.udpm.examdistribution.core.authentication.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/dang-nhap")
    public String viewAuthentication(){
        return "authentication/authentication";
    }

}
