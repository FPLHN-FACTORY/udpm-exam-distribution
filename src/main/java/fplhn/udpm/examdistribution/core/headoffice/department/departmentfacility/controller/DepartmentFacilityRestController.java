package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.controller;

import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateOrUpdateDepartmentFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.FindFacilityDetailRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.DepartmentFacilityService;
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
@RequestMapping(MappingConstants.API_HEAD_OFFICE_DEPARTMENT_FACILITY)
@RequiredArgsConstructor
public class DepartmentFacilityRestController {

    private final DepartmentFacilityService departmentFacilityService;

    /**
     * tìm kiếm + phân trang
     *
     * @param id
     * @param request
     * @return post
     */
    @GetMapping("/get-all-department-facility/{id}")
    public ResponseEntity<?> getAllDepartmentFacility(@PathVariable String id, FindFacilityDetailRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.getAllDepartmentFacility(id, request));
    }

    @PostMapping("/add-department-facility")
    public ResponseEntity<?> addDepartmentFacility(@RequestBody CreateOrUpdateDepartmentFacilityRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.addDepartmentFacility(request));
    }

    @PutMapping("/update-department-facility/{id}")
    public ResponseEntity<?> updateDepartmentFacility(@PathVariable String id, @RequestBody CreateOrUpdateDepartmentFacilityRequest request) {
        return Helper.createResponseEntity(departmentFacilityService.updateDepartmentFacility(request, id));
    }

    @PutMapping("/delete-department-facility/{id}")
    public ResponseEntity<?> deleteDepartmentFacility(@PathVariable String id) {
        return Helper.createResponseEntity(departmentFacilityService.deleteDepartmentFacility(id));
    }

    @GetMapping("/get-list-facility")
    public ResponseEntity<?> getListFacilityName() {
        return Helper.createResponseEntity(departmentFacilityService.getListFacility());
    }

    @GetMapping("/get-list-head-of-department")
    public ResponseEntity<?> getListHeadOfDepartment() {
        return Helper.createResponseEntity(departmentFacilityService.getListStaff());
    }

    @GetMapping("/get-department-name/{departmentId}")
    public ResponseEntity<?> getDepartmentName(@PathVariable String departmentId) {
        return Helper.createResponseEntity(departmentFacilityService.getDepartmentName(departmentId));
    }

}
