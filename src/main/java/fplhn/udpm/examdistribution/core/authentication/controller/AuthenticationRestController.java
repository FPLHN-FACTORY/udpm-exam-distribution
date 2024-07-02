package fplhn.udpm.examdistribution.core.authentication.controller;

import fplhn.udpm.examdistribution.core.authentication.service.AuthenticationService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_AUTHENTICATION)
public class AuthenticationRestController {

    private final AuthenticationService authenticationService;

    @GetMapping("/get-list-facility")
    public ResponseEntity<?> getListFacility() {
        return Helper.createResponseEntity(authenticationService.getListFacility());
    }

}
