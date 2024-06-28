package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.controller;

import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.MajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.UpdateMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.MajorFacilityService;
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
@RequestMapping(MappingConstants.API_HEAD_OFFICE_MAJOR_FACILITY)
@RequiredArgsConstructor
public class MajorFacilityRestController {

    private final MajorFacilityService majorFacilityService;

    @GetMapping
    public ResponseEntity<?> getMajorFacilities(MajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.getAllMajorFacilities(request));
    }

    @PostMapping
    public ResponseEntity<?> createMajorFacility(@RequestBody CreateMajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.createMajorFacility(request));
    }

    @PutMapping("/{majorFacilityId}")
    public ResponseEntity<?> updateMajorFacility(@PathVariable String majorFacilityId, @RequestBody UpdateMajorFacilityRequest request) {
        return Helper.createResponseEntity(majorFacilityService.updateMajorFacility(majorFacilityId, request));
    }

    @GetMapping("/{majorFacilityId}")
    public ResponseEntity<?> getMajorFacilityById(@PathVariable String majorFacilityId) {
        return Helper.createResponseEntity(majorFacilityService.getMajorFacilityById(majorFacilityId));
    }

    @GetMapping("/major/{departmentId}")
    public ResponseEntity<?> getMajorFacilities(@PathVariable String departmentId) {
        return Helper.createResponseEntity(majorFacilityService.getAllMajors(departmentId));
    }

}
