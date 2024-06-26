package fplhn.udpm.examdistribution.core.headoffice.department.department.controller;

import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateOrUpdateMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.service.MajorService;
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
@RequestMapping(MappingConstants.API_HEAD_OFFICE_MAJOR)
@RequiredArgsConstructor
public class MajorRestController {

    private final MajorService majorService;

    @GetMapping("/get-all-major/{departmentId}")
    public ResponseEntity<?> getAllMajor(@PathVariable String departmentId, FindMajorRequest request) {
        return Helper.createResponseEntity(majorService.getAllMajor(departmentId, request));
    }

    @PostMapping("/add-major")
    public ResponseEntity<?> addCoSo(@RequestBody CreateOrUpdateMajorRequest request) {
        return Helper.createResponseEntity(majorService.addMajor(request));
    }

    @PutMapping("/update-major/{id}")
    public ResponseEntity<?> updateCoSo(@RequestBody CreateOrUpdateMajorRequest request, @PathVariable String id) {
        return Helper.createResponseEntity(majorService.updateMajor(request, id));
    }

    @PutMapping("/delete-major/{id}")
    public ResponseEntity<?> deleteCoSo(@PathVariable String id) {
        return Helper.createResponseEntity(majorService.deleteMajor(id));
    }

}
