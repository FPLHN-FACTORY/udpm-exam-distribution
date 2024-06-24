package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HORoleStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
public class HOStaffRestController {

    private final HORoleStaffService staffService;

    public HOStaffRestController(HORoleStaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<?> getStaff(HOStaffRequest hoRoleStaffRequest) {
        return Helper.createResponseEntity(staffService.getStaffByRole(hoRoleStaffRequest));
    }

}
