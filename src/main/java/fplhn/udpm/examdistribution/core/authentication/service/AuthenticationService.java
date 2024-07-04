package fplhn.udpm.examdistribution.core.authentication.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    void authorSwitch(String role, String redirectUri, String facilityId, HttpServletRequest request, HttpServletResponse response) throws IOException;

    ResponseObject<?> getListFacility();

}
