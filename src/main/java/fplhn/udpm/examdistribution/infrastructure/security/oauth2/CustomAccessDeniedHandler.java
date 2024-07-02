package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        new DefaultRedirectStrategy().sendRedirect(
                request, response,
                MappingConstants.AUTHENTICATION + MappingConstants.REDIRECT_AUTHENTICATION_FORBIDDEN
        );
    }

}
