package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSExamShiftServiceRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSFileResourceResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_JOIN_ROOM)
public class HSExamShiftRestController {

    private final HSExamShiftService hsExamShiftService;

    @GetMapping
    public ResponseEntity<?> getAllExamShift() {
        return Helper.createResponseEntity(hsExamShiftService.getAllExamShift());
    }

    @PostMapping("/create")
    public ResponseEntity<?> createExamShift(@RequestBody HSCreateExamShiftRequest hsCreateExamShiftRequest) {
        return Helper.createResponseEntity(hsExamShiftService.createExamShift(hsCreateExamShiftRequest));
    }

    @PostMapping
    public ResponseEntity<?> joinExamShift(@RequestBody HSExamShiftServiceRequest joinRoomRequest) {
        return Helper.createResponseEntity(hsExamShiftService.joinExamShift(joinRoomRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hsExamShiftService.getExamShiftByCode(examShiftCode));
    }

    @GetMapping("/{examShiftCode}/count-student")
    public ResponseEntity<?> countStudentInExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hsExamShiftService.countStudentInExamShift(examShiftCode));
    }

    @GetMapping("/file-exam-rule")
    public ResponseEntity<?> getFileExamRule(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = hsExamShiftService.getFileExamRule(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            HSExamRuleResourceResponse fileResponse = (HSExamRuleResourceResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/path")
    public ResponseEntity<?> getExamPaperShiftInfoAndPathByExamShiftCode
            (@RequestParam(name = "examShiftCode") String examShiftCode) {
        ResponseObject<?> responseObject
                = hsExamShiftService.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getExamShiftPaperByExamShiftCode
            (@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = hsExamShiftService.getExamShiftPaperByExamShiftCode(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            HSFileResourceResponse fileResponse = (HSFileResourceResponse) responseObject.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition",
                            "inline; filename=" + fileResponse.getFileName())
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
