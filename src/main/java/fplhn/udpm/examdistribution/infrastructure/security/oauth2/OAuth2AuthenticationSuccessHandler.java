package fplhn.udpm.examdistribution.infrastructure.security.oauth2;

import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.infrastructure.config.global.GlobalVariables;
import fplhn.udpm.examdistribution.infrastructure.constant.CookieConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.Role;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthAssignUploaderRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthBlockRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthFacilityRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffMajorFacilityRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStaffRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository.AuthStudentRepository;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.response.BlockAndSemesterIdResponse;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomStudentCookie;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomUserCookie;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.OAuth2UserInfo;
import fplhn.udpm.examdistribution.utils.CookieUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final HttpSession httpSession;

    private final AuthStaffRepository authStaffRepository;

    private final AuthStudentRepository authStudentRepository;

    private final AuthAssignUploaderRepository assignUploaderRepository;

    private final AuthBlockRepository blockRepository;

    private final AuthFacilityRepository facilityRepository;

    private final AuthStaffMajorFacilityRepository staffMajorFacilityRepository;

    private final GlobalVariables globalVariables;

    private static final int COOKIE_EXPIRE = 7200; // 2 hours

    private String globalBlockId = "";

    private String globalSemesterId = "";

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
            this.setSessionAndCookie(request, response, authentication, userInfo);
        }
    }

    private void setSessionAndCookie(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            OAuth2UserInfo userInfo
    ) throws IOException {
        String isStudentSession = (String) httpSession.getAttribute(SessionConstant.IS_STUDENT);

        if (SessionConstant.IS_STUDENT.equalsIgnoreCase(isStudentSession)) {
            this.handleStudentSession(request, response, authentication, userInfo);
        } else {
            this.handleStaffSession(request, response, authentication, userInfo);
        }
    }

    private void handleStudentSession(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            OAuth2UserInfo userInfo
    ) throws IOException {
        Optional<Student> optionalStudent = authStudentRepository.isStudentExist(userInfo.getEmail());
        boolean isBlockAndSemesterId = this.checkBlockAndSemesterIdIsEmpty();
        if (optionalStudent.isEmpty()) {
            if (!isBlockAndSemesterId) {
                this.errorAuthentication(request, response, SessionConstant.ERROR_LOGIN_EMPTY_BLOCK_MESSAGE);
            } else {
                this.errorAuthentication(request, response, SessionConstant.ERROR_LOGIN_FORBIDDEN_MESSAGE);
            }
        } else {
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

            this.successAuthentication(request, response);
        }
    }

    private void handleStaffSession(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication,
            OAuth2UserInfo userInfo
    ) throws IOException {
        Optional<Staff> optionalStaff = authStaffRepository.getStaffByAccountFpt(userInfo.getEmail());

        String facilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString();
        globalVariables.setGlobalVariable(SessionConstant.CURRENT_USER_FACILITY_ID, facilityId);
        Optional<Facility> facility = facilityRepository.findFacilityById(facilityId);

        if (optionalStaff.isEmpty() || facility.isEmpty()) {
            this.errorAuthentication(request, response, SessionConstant.ERROR_LOGIN_FORBIDDEN_MESSAGE);
        } else {
            Staff currentStaff = optionalStaff.get();
            globalVariables.setGlobalVariable(SessionConstant.CURRENT_USER_ID, currentStaff.getId());

            Collection<? extends GrantedAuthority> role = authentication.getAuthorities();
            List<String> roleNames = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            if (roleNames.get(0).equalsIgnoreCase(Role.BAN_DAO_TAO.toString())) {
                CustomUserCookie userCookie = buildStaffAdminCookie(
                        currentStaff,
                        role.toString(),
                        userInfo,
                        facility.get()
                );
                String base64Encoded = CookieUtils.serializeAndEncode(userCookie);

                CookieUtils.addCookie(
                        response,
                        CookieConstant.EXAM_DISTRIBUTION_INFORMATION.getName(),
                        base64Encoded,
                        COOKIE_EXPIRE,
                        false,
                        false
                );

                this.successAuthentication(request, response);
            } else {
                boolean isBlockAndSemesterId = this.checkBlockAndSemesterIdIsEmpty();
                if (!isBlockAndSemesterId) {
                    this.errorAuthentication(request, response, SessionConstant.ERROR_LOGIN_EMPTY_BLOCK_MESSAGE);
                } else {
                    List<StaffMajorFacility> staffMajorFacilityOptional = staffMajorFacilityRepository.findByStaffIdAndFacilityId(currentStaff.getId(), facilityId);

                    if (staffMajorFacilityOptional.isEmpty()) {
                        this.errorAuthentication(request, response, SessionConstant.ERROR_LOGIN_FORBIDDEN_MESSAGE);
                    } else {
                        CustomUserCookie userCookie = buildStaffCookie(
                                staffMajorFacilityOptional.get(0),
                                currentStaff,
                                role.toString(),
                                userInfo
                        );
                        String base64Encoded = CookieUtils.serializeAndEncode(userCookie);

                        CookieUtils.addCookie(
                                response,
                                CookieConstant.EXAM_DISTRIBUTION_INFORMATION.getName(),
                                base64Encoded,
                                COOKIE_EXPIRE,
                                false,
                                false
                        );

                        this.successAuthentication(request, response);
                    }
                }
            }
        }
    }

    private CustomStudentCookie buildStudentCookie(Student student, String role, OAuth2UserInfo userInfo) {
        this.setCurrentStudentInformationSession(userInfo, student);
        return CustomStudentCookie.builder()
                .userEmail(userInfo.getEmail())
                .userId(student.getId())
                .userFullName(userInfo.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .build();
    }

    private CustomUserCookie buildStaffCookie(
            StaffMajorFacility staffMajorFacility,
            Staff staff,
            String role,
            OAuth2UserInfo userInfo
    ) {
        this.setCurrentUserInformationSession(staffMajorFacility, userInfo, staff, role);
        return CustomUserCookie.builder()
                .userId(staff.getId())
                .departmentFacilityId(staffMajorFacility.getMajorFacility().getDepartmentFacility().getId())
                .majorFacilityId(staffMajorFacility.getMajorFacility().getId())
                .departmentId(staffMajorFacility.getMajorFacility().getDepartmentFacility().getDepartment().getId())
                .departmentName(staffMajorFacility.getMajorFacility().getDepartmentFacility().getDepartment().getName())
                .facilityId(staffMajorFacility.getMajorFacility().getDepartmentFacility().getFacility().getId())
                .facilityName(staffMajorFacility.getMajorFacility().getDepartmentFacility().getFacility().getName())
                .userEmailFPT(staff.getAccountFpt())
                .userEmailFe(staff.getAccountFe())
                .userFullName(userInfo.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .isAssignUploader(httpSession.getAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER).toString())
                .build();
    }

    private CustomUserCookie buildStaffAdminCookie(
            Staff staff,
            String role,
            OAuth2UserInfo userInfo,
            Facility facility
    ) {
        this.setCurrentUserAdminInformationSession(userInfo, staff, role);

        return CustomUserCookie.builder()
                .userId(staff.getId())
                .facilityId(facility.getId())
                .facilityName(facility.getName())
                .userEmailFPT(staff.getAccountFpt())
                .userEmailFe(staff.getAccountFe())
                .userFullName(userInfo.getName())
                .userPicture(userInfo.getPicture())
                .userRole(role)
                .build();
    }

    private void setCurrentStudentInformationSession(OAuth2UserInfo userInfo, Student student) {
        httpSession.setAttribute(SessionConstant.CURRENT_USER_EMAIL, userInfo.getEmail());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_PICTURE, userInfo.getPicture());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ID, student.getId());
        this.setBlockAndSessionId();
        student.setPicture(userInfo.getPicture());
        authStudentRepository.save(student);
    }

    private void setCurrentUserInformationSession(
            StaffMajorFacility staffMajorFacility,
            OAuth2UserInfo userInfo,
            Staff staff,
            String role
    ) {
        httpSession.setAttribute(SessionConstant.CURRENT_USER_EMAIL, userInfo.getEmail());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_PICTURE, userInfo.getPicture());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ID, staff.getId());
        httpSession.setAttribute(
                SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID,
                staffMajorFacility.getMajorFacility().getDepartmentFacility().getId()
        );
        httpSession.setAttribute(
                SessionConstant.CURRENT_USER_MAJOR_FACILITY_ID,
                staffMajorFacility.getMajorFacility().getId()
        );
        httpSession.setAttribute(
                SessionConstant.CURRENT_USER_DEPARTMENT_ID,
                staffMajorFacility.getMajorFacility().getDepartmentFacility().getDepartment().getId()
        );
        httpSession.setAttribute(
                SessionConstant.CURRENT_USER_FACILITY_ID,
                staffMajorFacility.getMajorFacility().getDepartmentFacility().getFacility().getId()
        );
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ROLE, role);

        staff.setPicture(userInfo.getPicture());
        authStaffRepository.save(staff);

        BlockAndSemesterIdResponse blockAndSessionId = this.setBlockAndSessionId();

        Optional<AssignUploader> isAssignUploaderIsExist = assignUploaderRepository.findByStaffId(
                staff.getId(), blockAndSessionId.getSemesterId()
        );
        if (isAssignUploaderIsExist.isPresent()) {
            httpSession.setAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER, "TRUE");
        } else {
            httpSession.setAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER, "FALSE");
        }
    }

    private void setCurrentUserAdminInformationSession(OAuth2UserInfo userInfo, Staff staff, String role) {
        httpSession.setAttribute(SessionConstant.CURRENT_USER_EMAIL, userInfo.getEmail());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_PICTURE, userInfo.getPicture());
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ID, staff.getId());
        httpSession.setAttribute(
                SessionConstant.CURRENT_USER_FACILITY_ID,
                httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString()
        );
        httpSession.setAttribute(SessionConstant.CURRENT_USER_ROLE, role);

        staff.setPicture(userInfo.getPicture());
        authStaffRepository.save(staff);
    }

    private BlockAndSemesterIdResponse setBlockAndSessionId() {
        httpSession.setAttribute(SessionConstant.CURRENT_BLOCK_ID, globalBlockId);
        httpSession.setAttribute(SessionConstant.CURRENT_SEMESTER_ID, globalSemesterId);

        return new BlockAndSemesterIdResponse(globalBlockId, globalSemesterId);
    }

    private boolean checkBlockAndSemesterIdIsEmpty() {
        Long now = new Date().getTime();
        for (Block block : blockRepository.findAll()) {
            if (block.getStartTime() < now && now < block.getEndTime()) {
                globalBlockId = block.getId();
                break;
            }
        }

        Optional<Block> blockOptional = blockRepository.findById(globalBlockId);
        if (globalBlockId.isEmpty() || blockOptional.isEmpty()) {
            return false;
        }

        globalSemesterId = blockOptional.get().getSemester().getId();

        return true;
    }

    private void errorAuthentication(HttpServletRequest request, HttpServletResponse response, String errorMessage) throws IOException {
        httpSession.setAttribute(SessionConstant.ERROR_LOGIN, errorMessage);
        new DefaultRedirectStrategy().sendRedirect(request, response, "/");
    }

    private void successAuthentication(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String scheme = request.getScheme();
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();

        String urlRedirect = scheme + "://" + serverName + ":" + serverPort + "/" + httpSession.getAttribute(SessionConstant.REDIRECT_LOGIN).toString();
        log.info("Redirect to: {}", urlRedirect);
        new DefaultRedirectStrategy().sendRedirect(request, response, urlRedirect);
    }

}