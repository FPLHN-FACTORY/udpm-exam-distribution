package fplhn.udpm.examdistribution.core.student.examshift.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SOpenExamPaperRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SRefreshJoinRoomRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.response.SExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.student.examshift.model.response.SFileResourceResponse;
import fplhn.udpm.examdistribution.core.student.examshift.service.SExamShiftService;
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
@RequestMapping(MappingConstants.API_STUDENT_EXAM_SHIFT)
@RequiredArgsConstructor
public class SExamShiftRestController {

    private final SExamShiftService sExamShiftService;

    @PostMapping("/join")
    public ResponseEntity<?> joinExamShift(@RequestBody SExamShiftRequest sExamShiftRequest) {
        return Helper.createResponseEntity(sExamShiftService.joinExamShift(sExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(sExamShiftService.getExamShiftByCode(examShiftCode));
    }

    @GetMapping("/file-exam-rule")
    public ResponseEntity<?> getFileExamRule(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = sExamShiftService.getFileExamRule(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            SExamRuleResourceResponse fileResponse = (SExamRuleResourceResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition",
                            "inline; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/path")
    public ResponseEntity<?> getExamPaperShiftInfoAndPathByExamShiftCode
            (@RequestParam(name = "examShiftCode") String examShiftCode) {
        ResponseObject<?> responseObject
                = sExamShiftService.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getExamShiftPaperByExamShiftCode
            (@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = sExamShiftService.getExamShiftPaperByExamShiftCode(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            SFileResourceResponse fileResponse = (SFileResourceResponse) responseObject.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition",
                            "inline; filename=" + fileResponse.getFileName())
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/start-time-end-time")
    public ResponseEntity<?> getStartTimeEndTimeExamPaperByExamShiftCode
            (@RequestParam(name = "examShiftCode") String examShiftCode) {
        return Helper.createResponseEntity(sExamShiftService.getStartTimeEndTimeExamPaperByExamShiftCode(examShiftCode));
    }

    @PostMapping("/open")
    public ResponseEntity<?> openExamPaper(@RequestBody SOpenExamPaperRequest sOpenExamPaperRequest) {
        return Helper.createResponseEntity(sExamShiftService.openExamPaper(sOpenExamPaperRequest));
    }

    @PutMapping("/{examShiftCode}/update-exam-student-status")
    public ResponseEntity<?> updateExamStudentStatus(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(sExamShiftService.updateExamStudentStatus(examShiftCode));
    }

    @PutMapping("/refresh-join-room")
    public ResponseEntity<?> refreshJoinRoom(@RequestBody SRefreshJoinRoomRequest request){
        return Helper.createResponseEntity(sExamShiftService.refreshJoinRoom(request));
    }

}
