package fplhn.udpm.examdistribution.infrastructure.security.oauth2.user;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStudentRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final HttpSession httpSession;

    private final AuthStaffRepository authStaffRepository;

    private final AuthStudentRepository authStudentRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        this.clearSession();
        OAuth2User user = super.loadUser(userRequest);
        OAuth2UserInfo userInfo = new OAuth2UserInfo(user.getAttributes());

        String facilityId = (String) httpSession.getAttribute(SessionConstant.FACILITY_ID);
        String role = (String) httpSession.getAttribute(SessionConstant.ROLE_LOGIN);

        if (role.equalsIgnoreCase("SINH_VIEN")) {
            httpSession.setAttribute(SessionConstant.IS_STUDENT, SessionConstant.IS_STUDENT);
            Optional<Student> isStudentExist = authStudentRepository.isStudentExist(userInfo.getEmail());
            if (isStudentExist.isEmpty()) {
                Student postStudent = new Student();
                postStudent.setEmail(userInfo.getEmail());
                postStudent.setName(userInfo.getGivenName());
                postStudent.setStudentCode(userInfo.getEmail().split("@")[0]);
                postStudent.setStatus(EntityStatus.ACTIVE);
                authStudentRepository.save(postStudent);
            } else {
                Optional<Student> isStudentBan = authStudentRepository.isStudentExist(userInfo.getEmail());
                if (isStudentBan.get().getStatus().equals(EntityStatus.ACTIVE)) {
                    Student putStudent = isStudentBan.get();
                    putStudent.setName(userInfo.getGivenName());
                    putStudent.setStudentCode(userInfo.getEmail().split("@")[0]);
                    authStudentRepository.save(putStudent);
                } else {
                    httpSession.setAttribute(SessionConstant.ERROR_LOGIN, "Tài khoản không được phép đăng nhập vào hệ thống!");
                }
            }
        } else {
            Optional<Staff> isStaffExist = authStaffRepository.getStaffByAccountFptAndFacilityId(userInfo.getEmail(), facilityId, role);
            if (isStaffExist.isEmpty()) {
                httpSession.setAttribute(SessionConstant.ERROR_LOGIN, "Tài khoản không được phép đăng nhập vào hệ thống!");
            }
        }

        userInfo.setRole(role);
        return userInfo;
    }

    private void clearSession() {
        httpSession.removeAttribute(SessionConstant.ERROR_LOGIN);
        httpSession.removeAttribute(SessionConstant.EMAIL);
        httpSession.removeAttribute(SessionConstant.PICTURE);
    }

}
