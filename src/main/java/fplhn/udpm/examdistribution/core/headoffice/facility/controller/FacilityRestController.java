package fplhn.udpm.examdistribution.core.headoffice.facility.controller;

import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.service.FacilityChildService;
import fplhn.udpm.examdistribution.core.headoffice.facility.service.FacilityService;
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
@RequestMapping(MappingConstants.API_HEAD_OFFICE_FACILITY)
@RequiredArgsConstructor
public class FacilityRestController {

    private final FacilityService facilityService;

    private final FacilityChildService facilityChildService;

    @GetMapping
    public ResponseEntity<?> getAll(FacilityRequest request) {
        return Helper.createResponseEntity(facilityService.getAllFacility(request));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateUpdateFacilityRequest request) {
        return Helper.createResponseEntity(facilityService.createFacility(request));
    }

    @PutMapping("/{facilityId}")
    public ResponseEntity<?> update(@PathVariable String facilityId, @RequestBody CreateUpdateFacilityRequest request) {
        return Helper.createResponseEntity(facilityService.updateFacility(facilityId, request));
    }

    @PutMapping("/{facilityId}/change-status")
    public ResponseEntity<?> changeStatus(@PathVariable String facilityId) {
        return Helper.createResponseEntity(facilityService.changeFacilityStatus(facilityId));
    }

    @GetMapping("/{facilityId}")
    public ResponseEntity<?> getById(@PathVariable String facilityId) {
        return Helper.createResponseEntity(facilityService.getFacilityById(facilityId));
    }

    @GetMapping("/{facilityId}/facility-child")
    public ResponseEntity<?> getAllFacilityChild(@PathVariable String facilityId, FacilityRequest request) {
        return Helper.createResponseEntity(facilityChildService.getAllFacilityChild(facilityId,request));
    }

    @PostMapping("/{facilityId}/facility-child")
    public ResponseEntity<?> createFacilityChild(@PathVariable String facilityId,@RequestBody CreateUpdateFacilityChildRequest request) {
        return Helper.createResponseEntity(facilityChildService.createFacilityChild(facilityId,request));
    }

    @PutMapping("/{facilityId}/facility-child")
    public ResponseEntity<?> updateFacilityChild(@PathVariable String facilityId,@RequestBody CreateUpdateFacilityChildRequest request) {
        return Helper.createResponseEntity(facilityChildService.updateFacilityChild(facilityId,request));
    }

    @GetMapping("/facility-child/{facilityChildId}")
    public ResponseEntity<?> getFacilityChildById(@PathVariable String facilityChildId) {
        return Helper.createResponseEntity(facilityChildService.getFacilityByIdChild(facilityChildId));
    }

    @PutMapping("/facility-child/{facilityChildId}/change-status")
    public ResponseEntity<?> changeStatusFacilityChild(@PathVariable String facilityChildId) {
        return Helper.createResponseEntity(facilityChildService.changeFacilityStatusChild(facilityChildId));
    }

}
