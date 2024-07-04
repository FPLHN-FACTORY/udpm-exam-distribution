package fplhn.udpm.examdistribution.core.authentication.service.impl;

import fplhn.udpm.examdistribution.core.authentication.repository.AuthFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.authentication.service.AuthenticationService;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final HttpSession httpSession;

    private final AuthFacilityExtendRepository facilityRepository;

    @Override
    public void authorSwitch(String role, String redirectUri, String facilityId, HttpServletRequest request, HttpServletResponse response) throws IOException {
        httpSession.setAttribute(SessionConstant.ROLE_LOGIN, role);
        httpSession.setAttribute(SessionConstant.REDIRECT_LOGIN, redirectUri);
        httpSession.setAttribute(SessionConstant.FACILITY_ID, facilityId);

        response.sendRedirect(MappingConstants.REDIRECT_GOOGLE_AUTHORIZATION);
    }

    @Override
    public ResponseObject<?> getListFacility() {
        return new ResponseObject<>(facilityRepository.getListFacility(), HttpStatus.OK, "Lấy thành công danh sách cơ sở");
    }

}
