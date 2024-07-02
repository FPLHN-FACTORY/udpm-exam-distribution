package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession httpSession;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {
//        OAuth2UserInfo userInfo = (OAuth2UserInfo) authentication.getPrincipal();
        if (httpSession.getAttribute(SessionConstant.ROLE) == null || httpSession.getAttribute(SessionConstant.ERROR_LOGIN) != null) {
            new DefaultRedirectStrategy().sendRedirect(request, response, "/");
        } else {
            String serverName = request.getServerName();
            String serverPort = String.valueOf(request.getServerPort());

            String domain = "http://" + serverName + ":" + serverPort + "/" + httpSession.getAttribute(SessionConstant.REDIRECT_LOGIN).toString();
            new DefaultRedirectStrategy().sendRedirect(request, response, domain);
        }
    }

}
