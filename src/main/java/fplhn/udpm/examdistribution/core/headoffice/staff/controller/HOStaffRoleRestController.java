package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleChangePermissionRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffRoleService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
public class HOStaffRoleRestController {

    private final HOStaffRoleService staffRoleService;

    public HOStaffRoleRestController(HOStaffRoleService staffRoleService) {
        this.staffRoleService = staffRoleService;
    }

    @GetMapping("/role/{idStaff}")
    public ResponseEntity<?> getAll(@PathVariable(value = "idStaff") String idStaff) {
        return Helper.createResponseEntity(staffRoleService.getAllRole(idStaff));
    }

    @GetMapping("/role-check")
    public ResponseEntity<?> getRolesChecked(HOStaffRoleRequest hoRoleRequest) {
        return Helper.createResponseEntity(staffRoleService.getRolesChecked(hoRoleRequest));
    }

    @PutMapping("/change-permission")
    public ResponseEntity<?> updateStaffRole(@RequestBody HOStaffRoleChangePermissionRequest hoStaffRoleChangePermissionRequest) {
        return Helper.createResponseEntity(staffRoleService.updateStaffRole(hoStaffRoleChangePermissionRequest));
    }

}
