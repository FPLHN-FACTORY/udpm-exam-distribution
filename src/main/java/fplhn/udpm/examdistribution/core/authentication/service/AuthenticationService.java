package fplhn.udpm.examdistribution.core.authentication.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public interface AuthenticationService {

    void authorSwitch(String screen, String redirectUri, HttpServletRequest request, HttpServletResponse response) throws IOException;

}
