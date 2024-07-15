package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import com.fasterxml.jackson.core.JsonProcessingException;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.infrastructure.constant.CookieConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthAssignUploaderRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthBlockRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStudentRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomStudentCookie;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomUserCookie;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.OAuth2UserInfo;
import fplhn.udpm.examdistribution.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession httpSession;

    private final AuthStaffRepository authStaffRepository;

    private final AuthStudentRepository authStudentRepository;

    private final AuthAssignUploaderRepository assignUploaderRepository;

    private final AuthBlockRepository blockRepository;

    private static final int COOKIE_EXPIRE = 7200;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {
        OAuth2UserInfo userInfo = (OAuth2UserInfo) authentication.getPrincipal();

        if (httpSession.getAttribute(SessionConstant.ERROR_LOGIN) != null) {
            new DefaultRedirectStrategy().sendRedirect(request, response, "/");
        } else {
            this.setCookie(response, authentication, userInfo);

            String scheme = request.getScheme();
            String serverName = request.getServerName();
            int serverPort = request.getServerPort();

            StringBuilder origin = new StringBuilder();
            origin.append(scheme).append("://").append(serverName).append(":").append(serverPort).append("/");
            String urlRedirect = origin + httpSession.getAttribute(SessionConstant.REDIRECT_LOGIN).toString();

            new DefaultRedirectStrategy().sendRedirect(request, response, urlRedirect);
        }
    }

    private void setCookie(HttpServletResponse response, Authentication authentication, OAuth2UserInfo userInfo) throws JsonProcessingException {
        String isStudentSession = (String) httpSession.getAttribute(SessionConstant.IS_STUDENT);

        if (SessionConstant.IS_STUDENT.equalsIgnoreCase(isStudentSession)) {
            handleStudentSession(response, authentication, userInfo);
        } else {
            handleStaffSession(response, authentication, userInfo);
        }
    }

    private void handleStudentSession(HttpServletResponse response, Authentication authentication, OAuth2UserInfo userInfo) throws JsonProcessingException {
        Optional<Student> optionalStudent = authStudentRepository.isStudentExist(userInfo.getEmail());

        Student currentStudent = optionalStudent.get();
        String role = authentication.getAuthorities().toString();

        CustomStudentCookie userCookie = buildStudentCookie(currentStudent, role, userInfo);
        String base64Encoded = CookieUtils.serializeAndEncode(userCookie);

        CookieUtils.addCookie(
                response,
                CookieConstant.EXAM_DISTRIBUTION_INFORMATION.getName(),
                base64Encoded,
                COOKIE_EXPIRE,
                false,
                false
        );
    }

    private void handleStaffSession(HttpServletResponse response, Authentication authentication, OAuth2UserInfo userInfo) throws JsonProcessingException {
        Optional<Staff> optionalStaff = authStaffRepository.getStaffByAccountFpt(userInfo.getEmail());

        Staff currentStaff = optionalStaff.get();
        String role = authentication.getAuthorities().toString();

        CustomUserCookie userCookie = buildStaffCookie(currentStaff, role, userInfo);
        String base64Encoded = CookieUtils.serializeAndEncode(userCookie);

        CookieUtils.addCookie(
                response,
                CookieConstant.EXAM_DISTRIBUTION_INFORMATION.getName(),
                base64Encoded,
                COOKIE_EXPIRE,
                false,
                false
        );
    }

    private CustomStudentCookie buildStudentCookie(Student student, String role, OAuth2UserInfo userInfo) {
        setCurrentStudentInformationSession(userInfo, student);
        return CustomStudentCookie.builder()
                .userEmail(userInfo.getEmail())
                .userId(student.getId())
                .userFullName(userInfo.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .build();
    }

    private CustomUserCookie buildStaffCookie(Staff staff, String role, OAuth2UserInfo userInfo) {
        setCurrentUserInformationSession(userInfo, staff, role);
        return CustomUserCookie.builder()
                .userId(staff.getId())
                .departmentFacilityId(staff.getDepartmentFacility().getId())
                .departmentName(staff.getDepartmentFacility().getDepartment().getName())
                .facilityName(staff.getDepartmentFacility().getFacility().getId())
                .facilityId(staff.getDepartmentFacility().getFacility().getId())
                .userEmailFPT(staff.getAccountFpt())
                .userEmailFe(staff.getAccountFe())
                .userFullName(userInfo.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .isAssignUploader(httpSession.getAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER).toString())
                .build();
    }

    private void setCurrentStudentInformationSession(OAuth2UserInfo userInfo, Student student) {
        httpSession.setAttribute(SessionConstant.CURRENT_USER_EMAIL, userInfo.getEmail());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_PICTURE, userInfo.getPicture());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ID, student.getId());

        student.setPicture(userInfo.getPicture());
        authStudentRepository.save(student);
    }

    private void setCurrentUserInformationSession(OAuth2UserInfo userInfo, Staff staff, String role) {
        httpSession.setAttribute(SessionConstant.CURRENT_USER_EMAIL, userInfo.getEmail());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_PICTURE, userInfo.getPicture());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ID, staff.getId());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_FACILITY_ID, staff.getDepartmentFacility().getFacility().getId());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_ID, staff.getDepartmentFacility().getDepartment().getId());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID, staff.getDepartmentFacility().getId());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ROLE, role);

        staff.setPicture(userInfo.getPicture());
        authStaffRepository.save(staff);

        String blockId = "";
        Long now = new Date().getTime();
        for (Block block : blockRepository.findAll()) {
            if (block.getStartTime() < now && now < block.getEndTime()) {
                blockId = block.getId();
                break;
            }
        }
        String semesterId = blockRepository.findById(blockId).get().getSemester().getId();

        httpSession.setAttribute(SessionConstant.CURRENT_BLOCK_ID, blockId);
        httpSession.setAttribute(SessionConstant.CURRENT_SEMESTER_ID, semesterId);

        Optional<AssignUploader> isAssignUploaderIsExist = assignUploaderRepository.findByStaffId(staff.getId(), semesterId);
        if (isAssignUploaderIsExist.isPresent()) {
            httpSession.setAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER, "TRUE");
        } else {
            httpSession.setAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER, "FALSE");
        }
    }

}
