package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TJoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TFileResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_EXAM_SHIFT)
public class TExamShiftRestController {

    private final TExamShiftService tExamShiftService;

    @GetMapping
    public ResponseEntity<?> getAllExamShift() {
        return Helper.createResponseEntity(tExamShiftService.getAllExamShift());
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tExamShiftService.getExamShiftByCode(examShiftCode));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinExamShift(@RequestBody TJoinExamShiftRequest tJoinExamShiftRequest) {
        return Helper.createResponseEntity(tExamShiftService.joinExamShift(tJoinExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}/count-student")
    public ResponseEntity<?> countStudentInExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tExamShiftService.countStudentInExamShift(examShiftCode));
    }

    @PutMapping("/{examShiftCode}/remove-student/{studentId}")
    public ResponseEntity<?> removeStudent(
            @PathVariable String examShiftCode, @PathVariable String studentId, @RequestBody String reason) {
        return Helper.createResponseEntity(tExamShiftService.removeStudent(examShiftCode, studentId, reason));
    }

    @PutMapping("/{examShiftCode}/approve-student/{studentId}")
    public ResponseEntity<?> approveStudent(@PathVariable String examShiftCode, @PathVariable String studentId) {
        return Helper.createResponseEntity(tExamShiftService.approveStudent(examShiftCode, studentId));
    }

    @PutMapping("/{examShiftCode}/refuse-student/{studentId}")
    public ResponseEntity<?> refuseStudent(@PathVariable String examShiftCode, @PathVariable String studentId) {
        return Helper.createResponseEntity(tExamShiftService.refuseStudent(examShiftCode, studentId));
    }

    @PutMapping(value = "/{examShiftCode}/start")
    public ResponseEntity<?> startExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tExamShiftService.startExamShift(examShiftCode));
    }

    @PutMapping(value = "/{examShiftCode}/start-time")
    public ResponseEntity<?> startTime(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tExamShiftService.startTime(examShiftCode));
    }

    @GetMapping("/path")
    public ResponseEntity<?> getPathByExamShiftCode(@RequestParam(name = "examShiftCode") String examShiftCode) {
        ResponseObject<?> responseObject = tExamShiftService.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = tExamShiftService.getFile(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            TFileResourceResponse fileResponse = (TFileResourceResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file-exam-rule")
    public ResponseEntity<?> getFileExamRule(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = tExamShiftService.getFileExamRule(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            TExamRuleResourceResponse fileResponse = (TExamRuleResourceResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

    @PutMapping("/{examShiftCode}/update-status")
    public ResponseEntity<?> updateStatusExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(tExamShiftService.updateStatusExamShift(examShiftCode));
    }

}
