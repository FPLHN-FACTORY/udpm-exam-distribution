package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleChangePermissionRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffMajorFacilityService;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffRoleService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
@RequiredArgsConstructor
public class HOStaffRoleRestController {

    private final HOStaffRoleService staffRoleService;

    private final HOStaffMajorFacilityService hoStaffMajorFacilityService;

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

    @GetMapping("/major-facility/{idStaff}")
    public ResponseEntity<?> getMajorFacilities(@PathVariable(value = "idStaff") String idStaff) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.getStaffMajorFacilities(idStaff));
    }

    @GetMapping("/department/{idFacility}")
    public ResponseEntity<?> getDepartmentByFacility(@PathVariable(value = "idFacility") String idFacility) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.getDepartmentByFacility(idFacility));
    }

    @GetMapping("/major")
    public ResponseEntity<?> getMajorByDepartmentFacility(@RequestParam(value = "idDepartment", defaultValue = "0") String idDepartment,
                                                         @RequestParam(value = "idFacility", defaultValue = "0") String idFacility) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.getMajorByDepartmentFacility(idDepartment,idFacility));
    }

    @GetMapping("/facilities")
    public ResponseEntity<?> getFacilities() {
        return Helper.createResponseEntity(staffRoleService.getFacilities());
    }

    @GetMapping("/facilities-select")
    public ResponseEntity<?> getFacilitiesSelect(@RequestParam(value = "idStaff", defaultValue = "0") String idStaff) {
        return Helper.createResponseEntity(staffRoleService.getFacilitiesSelect(idStaff));
    }

    @PostMapping("/staff-major-facility")
    public ResponseEntity<?> createStaffMajorFacility(@RequestBody HOStaffMajorFacilityRequest staffMajorFacilityRequest) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.createStaffMajorFacility(staffMajorFacilityRequest));
    }

    @PutMapping("/staff-major-facility")
    public ResponseEntity<?> updateStaffMajorFacility(@RequestBody HOStaffMajorFacilityRequest staffMajorFacilityRequest) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.updateStaffMajorFacility(staffMajorFacilityRequest));
    }

    @DeleteMapping("/staff-major-facility")
    public ResponseEntity<?> deleteStaffMajorFacility(@RequestParam(value = "idStaffMajorFacility",defaultValue = "0")String idStaffMajorFacility) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.deleteStaffMajorFacility(idStaffMajorFacility));
    }

    @GetMapping("/staff-major-facility")
    public ResponseEntity<?> detailStaffMajorFacility(@RequestParam(value = "idStaffMajorFacility",defaultValue = "0")String idStaffMajorFacility) {
        return Helper.createResponseEntity(hoStaffMajorFacilityService.detailStaffMajorFacility(idStaffMajorFacility));
    }

}
