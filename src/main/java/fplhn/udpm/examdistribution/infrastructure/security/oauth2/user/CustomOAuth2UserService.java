package fplhn.udpm.examdistribution.infrastructure.security.oauth2.user;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final HttpSession httpSession;

    private final AuthStaffRepository authStaffRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        this.clearSession();

        OAuth2User user = super.loadUser(userRequest);
        OAuth2UserInfo userInfo = new OAuth2UserInfo(user.getAttributes());
        String facilityId = (String) httpSession.getAttribute(SessionConstant.FACILITY_ID);

        Optional<Staff> isStaffExist = authStaffRepository.getStaffByAccountFptAndFacilityId(userInfo.getEmail(), facilityId);

        if (isStaffExist.isEmpty()) {
            httpSession.setAttribute(SessionConstant.ERROR_LOGIN, "Tài khoản không được phép đăng nhập vào hệ thống!");
        }
        List<String> listRole = authStaffRepository.getListRoleByEmail(userInfo.getEmail(), facilityId);
        String screen = (String) httpSession.getAttribute(SessionConstant.SCREEN_LOGIN);

        if (!isUserHasRole(screen, listRole)) {
            httpSession.setAttribute(SessionConstant.ERROR_LOGIN, "Tài khoản không được phép đăng nhập vào hệ thống!");
        }

        userInfo.setRole(httpSession.getAttribute(SessionConstant.ROLE) != null && !((String) httpSession.getAttribute(SessionConstant.ROLE)).isEmpty()
                ? (String) httpSession.getAttribute(SessionConstant.ROLE)
                : "null");

        return userInfo;
    }

    private boolean isUserHasRole(String screen, List<String> listRole) {
        Optional<String> matchingRole = listRole.stream()
                .filter(role -> (screen.equalsIgnoreCase("TM_CNBM") &&
                                 (role.equalsIgnoreCase("CHU_NHIEM_BO_MON") || role.equalsIgnoreCase("TRUONG_MON"))) ||
                                screen.equalsIgnoreCase(role))
                .findFirst();

        matchingRole.ifPresent(role -> httpSession.setAttribute(SessionConstant.ROLE, role));
        return matchingRole.isPresent();
    }

    private void clearSession() {
        httpSession.removeAttribute(SessionConstant.ERROR_LOGIN);
        httpSession.removeAttribute(SessionConstant.ROLE);
        httpSession.removeAttribute(SessionConstant.EMAIL);
        httpSession.removeAttribute(SessionConstant.PICTURE);
//        httpSession.removeAttribute(SessionConstant.SCREEN_LOGIN);
//        httpSession.removeAttribute(SessionConstant.REDIRECT_LOGIN);
    }

}
