package fplhn.udpm.examdistribution.core.headoffice.department.department.controller;

import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateUpdateDepartmentRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindDepartmentsRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.service.DepartmentService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_DEPARTMENT)
@RequiredArgsConstructor
public class DepartmentRestController {

    private final DepartmentService departmentService;

    @GetMapping("/get-all-department")
    public ResponseEntity<?> getAllDepartment(FindDepartmentsRequest request) {
        return Helper.createResponseEntity(departmentService.getAllDepartment(request));
    }

    @GetMapping("/get-department/{id}")
    public ResponseEntity<?> getDetailDepartment(@PathVariable String id) {
        return Helper.createResponseEntity(departmentService.getDetailDepartment(id));
    }

    @PostMapping("/add-department")
    public ResponseEntity<?> addDepartment(@RequestBody CreateUpdateDepartmentRequest request) {
        return Helper.createResponseEntity(departmentService.addDepartment(request));
    }

    @PutMapping("/update-department/{id}")
    public ResponseEntity<?> updateDepartment(@RequestBody CreateUpdateDepartmentRequest request, @PathVariable String id) {
        return Helper.createResponseEntity(departmentService.updateDepartment(request,id));
    }

    @PutMapping("/delete-department/{id}")
    public ResponseEntity<?> deleteDepartment(@PathVariable String id) {
        return Helper.createResponseEntity(departmentService.deleteDepartment(id));
    }

    @GetMapping("/get-list-department")
    public ResponseEntity<?> getListDepartment() {
        return Helper.createResponseEntity(departmentService.getListDepartment());
    }

}
