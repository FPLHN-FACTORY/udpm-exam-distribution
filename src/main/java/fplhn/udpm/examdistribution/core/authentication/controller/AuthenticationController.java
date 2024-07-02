package fplhn.udpm.examdistribution.core.authentication.controller;

import fplhn.udpm.examdistribution.core.authentication.service.AuthenticationService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.AUTHENTICATION)
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @GetMapping(MappingConstants.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH)
    public void authorSwitch(
            @RequestParam(name = "screen", required = false) String screen,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.authorSwitch(screen, redirectUri, request, response);
    }

    @GetMapping(MappingConstants.REDIRECT_AUTHENTICATION_FORBIDDEN)
    public String forbiddenPage() {
        return "/error/403";
    }

    @GetMapping(MappingConstants.REDIRECT_AUTHENTICATION_UNAUTHORIZE)
    public String unAuthorizePage() {
        return "/error/401";
    }

}
