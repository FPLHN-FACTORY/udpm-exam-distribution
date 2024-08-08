package fplhn.udpm.examdistribution.core.headdepartment.examshift.controller;

import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ModifyExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.service.ManageExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("{examShiftId}")
    public ResponseEntity<?> getExamShiftDetail(@PathVariable String examShiftId) {
        return Helper.createResponseEntity(manageExamShiftService.getExamShiftDetail(examShiftId));
    }

    @PutMapping("{examShiftId}")
    public ResponseEntity<?> modifyExamShift(
            @PathVariable String examShiftId,
            @RequestBody ModifyExamShiftRequest request
    ) {
        return Helper.createResponseEntity(manageExamShiftService.editExamShift(examShiftId, request));
    }

    @GetMapping("/block-info")
    public ResponseEntity<?> getBlockInfo(String semesterId) {
        return Helper.createResponseEntity(manageExamShiftService.getBlockInfo(semesterId));
    }

    @GetMapping("/history")
    public ResponseEntity<?> getChangeHistory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "50") int size
    ) {
        return Helper.createResponseEntity(manageExamShiftService.getLogsImportStaff(page, size));
    }

}
