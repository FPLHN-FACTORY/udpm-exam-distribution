package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomUserCookie;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.OAuth2UserInfo;
import fplhn.udpm.examdistribution.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(OAuth2AuthenticationSuccessHandler.class);
    private final HttpSession httpSession;

    private final AuthStaffRepository authStaffRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2UserInfo userInfo = (OAuth2UserInfo) authentication.getPrincipal();

        if (httpSession.getAttribute(SessionConstant.ROLE) == null || httpSession.getAttribute(SessionConstant.ERROR_LOGIN) != null) {
            new DefaultRedirectStrategy().sendRedirect(request, response, "/"); //trở về trang đăng nhập với lỗi.
        } else {
            this.setCookie(response, authentication, userInfo);

            String serverName = request.getServerName();
            String serverPort = String.valueOf(request.getServerPort());

            String domain = "http://" + serverName + ":" + serverPort + "/" + httpSession.getAttribute(SessionConstant.REDIRECT_LOGIN).toString();
            new DefaultRedirectStrategy().sendRedirect(request, response, domain); //di chuyển đến trang người dùng chọn.
        }
    }

    private void setCookie(HttpServletResponse response, Authentication authentication, OAuth2UserInfo userInfo) throws JsonProcessingException {
        Optional<Staff> optionalStaff = authStaffRepository.getStaffByAccountFpt(userInfo.getEmail());
        Staff currentStaff = optionalStaff.get();
        String role = authentication.getAuthorities().toString();

        CustomUserCookie userCookie = CustomUserCookie.builder()
                .userId(currentStaff.getId())
                .departmentFacilityId(currentStaff.getDepartmentFacility().getId())
                .departmentName(currentStaff.getDepartmentFacility().getDepartment().getName())
                .facilityName(currentStaff.getDepartmentFacility().getFacility().getId())
                .userEmailFPT(currentStaff.getAccountFpt())
                .userEmailFe(currentStaff.getAccountFe())
                .userFullName(currentStaff.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .build();
        String base64Encoded = CookieUtils.serializeAndEncode(userCookie);

        CookieUtils.addCookie(response, "e_d_i", base64Encoded, 10000);

//        CookieUtils.addCookie(response, "u_roles", role, 10000);
//        CookieUtils.addCookie(response, "df_id", departmentFacility.getId(), 10000);
//        CookieUtils.addCookie(response, "d_name", departmentFacility.getDepartment().getName(), 10000);
//        CookieUtils.addCookie(response, "f_name", departmentFacility.getFacility().getId(), 10000);
//        CookieUtils.addCookie(response, "u_pic", userInfo.getPicture(), 10000);
//        CookieUtils.addCookie(response, "u_e_fpt", currentStaff.getAccountFpt(), 10000);
//        CookieUtils.addCookie(response, "u_e_fe", currentStaff.getAccountFe(), 10000);
//        CookieUtils.addCookie(response, "u_fn", "'" + userInfo.getGivenName() + "'", 10000);
//        CookieUtils.addCookie(response, "u_id", userInfo.getId(), 10000);
    }

}
