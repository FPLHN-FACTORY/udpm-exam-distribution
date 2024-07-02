package fplhn.udpm.examdistribution.core.authentication.controller;

import fplhn.udpm.examdistribution.core.authentication.service.AuthenticationService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.REDIRECT_AUTHENTICATION_AUTHOR_SWITCH)
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @GetMapping
    public void authorSwitch(
            @RequestParam(name = "screen", required = false) String screen,
            @RequestParam(name = "redirect_uri", required = false) String redirectUri,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        authenticationService.authorSwitch(screen, redirectUri, request, response);
    }

}
