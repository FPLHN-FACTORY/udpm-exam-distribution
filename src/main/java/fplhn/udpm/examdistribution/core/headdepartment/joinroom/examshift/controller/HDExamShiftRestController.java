package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDExamRuleResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDFileResourceResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.HDExamShiftService;
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
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM)
public class HDExamShiftRestController {

    private final HDExamShiftService hdExamShiftService;

    @GetMapping
    public ResponseEntity<?> getAllExamShift() {
        return Helper.createResponseEntity(hdExamShiftService.getAllExamShift());
    }

    @PostMapping
    public ResponseEntity<?> joinExamShift(@RequestBody HDExamShiftRequest joinRoomRequest) {
        return Helper.createResponseEntity(hdExamShiftService.joinExamShift(joinRoomRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hdExamShiftService.getExamShiftByCode(examShiftCode));
    }

    @GetMapping("/{examShiftCode}/count-student")
    public ResponseEntity<?> countStudentInExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hdExamShiftService.countStudentInExamShift(examShiftCode));
    }

    @GetMapping("/file-exam-rule")
    public ResponseEntity<?> getFileExamRule(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = hdExamShiftService.getFileExamRule(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            HDExamRuleResourceResponse fileResponse = (HDExamRuleResourceResponse) responseObject.getData();
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
                = hdExamShiftService.getExamPaperShiftInfoAndPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getExamShiftPaperByExamShiftCode
            (@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = hdExamShiftService.getExamShiftPaperByExamShiftCode(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            HDFileResourceResponse fileResponse = (HDFileResourceResponse) responseObject.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition",
                            "inline; filename=" + fileResponse.getFileName())
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
