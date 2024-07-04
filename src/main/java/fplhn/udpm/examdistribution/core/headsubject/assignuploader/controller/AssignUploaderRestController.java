package fplhn.udpm.examdistribution.core.headsubject.assignuploader.controller;

import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER)
@RequiredArgsConstructor
public class AssignUploaderRestController {

    private final AssignUploaderService assignUploaderService;

    @GetMapping("/subject/{departmentFacilityId}")
    public ResponseEntity<?> getAllSubject(@PathVariable String departmentFacilityId, FindSubjectRequest request) {
        return Helper.createResponseEntity(assignUploaderService.getAllSubject(departmentFacilityId, request));
    }

    @GetMapping("/staff/{departmentFacilityId}")
    public ResponseEntity<?> getAllStaff(@PathVariable String departmentFacilityId, FindStaffRequest request) {
        return Helper.createResponseEntity(assignUploaderService.getAllStaff(departmentFacilityId, request));
    }

    @PostMapping("/assign-uploader")
    public ResponseEntity<?> addOrDelAssignUploader(@RequestBody AssignUploaderRequest request) {
        return Helper.createResponseEntity(assignUploaderService.addOrDelAssignUploader(request));
    }

}
