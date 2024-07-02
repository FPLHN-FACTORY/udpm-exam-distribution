package fplhn.udpm.examdistribution.core.authentication.service.impl;

import fplhn.udpm.examdistribution.core.authentication.service.AuthenticationService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final HttpSession httpSession;

    @Override
    public void authorSwitch(String screen, String redirectUri, HttpServletRequest request, HttpServletResponse response) throws IOException {
        httpSession.setAttribute(SessionConstant.SCREEN_LOGIN, screen);
        httpSession.setAttribute(SessionConstant.REDIRECT_LOGIN, redirectUri);

        response.sendRedirect(MappingConstants.REDIRECT_GOOGLE_AUTHORIZATION);
    }

}
