package fplhn.udpm.examdistribution.core.headoffice.role.controller;

import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HORoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HOSaveRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.role.service.impl.HORoleServiceImpl;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_ROLE)
@RequiredArgsConstructor
public class HORoleRestController {

    private final HORoleServiceImpl roleService;

    @GetMapping
    public ResponseEntity<?> getAll(HORoleRequest hoRoleRequest) {
        return Helper.createResponseEntity(roleService.getAllRole(hoRoleRequest));
    }

    @GetMapping("/facilities")
    public ResponseEntity<?> getFacilities() {
        return Helper.createResponseEntity(roleService.getFacilities());
    }

    @PostMapping
    public ResponseEntity<?> addRole(@Valid @RequestBody  HOSaveRoleRequest hoRoleRequest) {
        return Helper.createResponseEntity(roleService.saveRole(hoRoleRequest));
    }

    @PutMapping
    public ResponseEntity<?> updateRole(@Valid @RequestBody HOSaveRoleRequest hoRoleRequest) {
        return Helper.createResponseEntity(roleService.saveRole(hoRoleRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneRole(@PathVariable(value = "id") String id) {
        return Helper.createResponseEntity(roleService.getOneRole(id));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteRole(@RequestParam(value = "id") String id) {
        return Helper.createResponseEntity(roleService.deleteRole(id));
    }
}
