package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import fplhn.udpm.examdistribution.infrastructure.constant.CookieConstant;
import fplhn.udpm.examdistribution.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        CookieUtils.deleteCookie(request, response, CookieConstant.EXAM_DISTRIBUTION_INFORMATION.getName());
    }

}
