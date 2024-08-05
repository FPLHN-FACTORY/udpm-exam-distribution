package fplhn.udpm.examdistribution.core.headoffice.staff.controller;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOSaveStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
@RequiredArgsConstructor
public class HOStaffRestController {

    private final HOStaffService staffService;

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

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        return Helper.createResponseEntity(staffService.getLogsImportStaff(page, size));
    }

}
