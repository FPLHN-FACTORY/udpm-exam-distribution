package fplhn.udpm.examdistribution.core.authentication.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_AUTHENTICATION_STUDENT)
@RequiredArgsConstructor
public class ViewStudentAuthenticationController {

    private final HttpSession httpSession;

    private final SecurityContextHolderStrategy securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

    @GetMapping
    public String viewAuthentication(Model model) {
        if (httpSession.getAttribute(SessionConstant.ERROR_LOGIN) != null) {
            this.clearSecurityContext();
            model.addAttribute("error", httpSession.getAttribute(SessionConstant.ERROR_LOGIN));
        }
        return "authentication/student-authentication";
    }

    private void clearSecurityContext() {
        SecurityContext context = this.securityContextHolderStrategy.getContext();
        this.securityContextHolderStrategy.clearContext();
        context.setAuthentication(null);
    }

}
