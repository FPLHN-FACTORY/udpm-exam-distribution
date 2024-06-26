package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOSaveStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
public class HOStaffRestController {

    private final HOStaffService staffService;

    public HOStaffRestController(HOStaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<?> getStaff(HOStaffRequest hoRoleStaffRequest) {
        return Helper.createResponseEntity(staffService.getStaffByRole(hoRoleStaffRequest));
    }

    @GetMapping("/departments-facilities")
    public ResponseEntity<?> getDepartmentFacility() {
        return Helper.createResponseEntity(staffService.getDepartmentFacility());
    }

    @GetMapping("/{staffId}")
    public ResponseEntity<?> getOneStaff(@PathVariable(value = "staffId") String staffId) {
        return Helper.createResponseEntity(staffService.detailStaff(staffId));
    }

    @PostMapping
    public ResponseEntity<?> createStaff(@Valid @RequestBody HOSaveStaffRequest staffRequest) {
        return Helper.createResponseEntity(staffService.createStaff(staffRequest));
    }

    @PutMapping
    public ResponseEntity<?> updateStaff(@Valid @RequestBody HOSaveStaffRequest staffRequest) {
        return Helper.createResponseEntity(staffService.updateStaff(staffRequest));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteStaff(@RequestParam(value = "id") String id) {
        return Helper.createResponseEntity(staffService.deleteStaff(id));
    }

}
