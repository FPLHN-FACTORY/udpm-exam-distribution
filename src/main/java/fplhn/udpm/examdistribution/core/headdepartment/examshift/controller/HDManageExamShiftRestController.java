package fplhn.udpm.examdistribution.core.headdepartment.examshift.controller;

import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.service.ManageExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_EXAM_SHIFT)
@RequiredArgsConstructor
public class HDManageExamShiftRestController {

    private final ManageExamShiftService manageExamShiftService;

    @GetMapping
    public ResponseEntity<?> getExamShifts(ExamShiftRequest request) {
        return Helper.createResponseEntity(manageExamShiftService.getAllExamShifts(request));
    }

    @GetMapping("/block-info")
    public ResponseEntity<?> getBlockInfo(String semesterId) {
        return Helper.createResponseEntity(manageExamShiftService.getBlockInfo(semesterId));
    }

}