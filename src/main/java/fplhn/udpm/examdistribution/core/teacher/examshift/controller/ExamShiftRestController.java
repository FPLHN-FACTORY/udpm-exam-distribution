package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.CreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.JoinExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.FileResourceResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.ExamShiftService;
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
public class ExamShiftRestController {

    private final ExamShiftService examShiftService;

    @PostMapping
    public ResponseEntity<?> createExamShift(@RequestBody CreateExamShiftRequest createExamShiftRequest) {
        return Helper.createResponseEntity(examShiftService.createExamShift(createExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(examShiftService.getExamShiftByCode(examShiftCode));
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinExamShift(@RequestBody JoinExamShiftRequest joinExamShiftRequest) {
        return Helper.createResponseEntity(examShiftService.joinExamShift(joinExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}/count-student")
    public ResponseEntity<?> countStudentInExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(examShiftService.countStudentInExamShift(examShiftCode));
    }

    @PutMapping("/{examShiftCode}/remove-student/{studentId}")
    public ResponseEntity<?> removeStudent(
            @PathVariable String examShiftCode, @PathVariable String studentId, @RequestBody String reason) {
        return Helper.createResponseEntity(examShiftService.removeStudent(examShiftCode, studentId, reason));
    }

    @PutMapping("/{examShiftCode}/approve-student/{studentId}")
    public ResponseEntity<?> approveStudent(@PathVariable String examShiftCode, @PathVariable String studentId) {
        return Helper.createResponseEntity(examShiftService.approveStudent(examShiftCode, studentId));
    }

    @PutMapping("/{examShiftCode}/refuse-student/{studentId}")
    public ResponseEntity<?> refuseStudent(@PathVariable String examShiftCode, @PathVariable String studentId) {
        return Helper.createResponseEntity(examShiftService.refuseStudent(examShiftCode, studentId));
    }

    @PutMapping("/{examShiftCode}/start")
    public ResponseEntity<?> startExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(examShiftService.startExamShift(examShiftCode));
    }

    @GetMapping("/path")
    public ResponseEntity<?> getPathByExamShiftCode(@RequestParam(name = "examShiftCode") String examShiftCode) {
        ResponseObject<?> responseObject = examShiftService.getPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = examShiftService.getFile(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            FileResourceResponse fileResponse = (FileResourceResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
