package fplhn.udpm.examdistribution.core.student.home.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.home.model.request.StudentExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.home.service.StudentExamShiftService;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.FileResourceResponse;
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
@RequestMapping(MappingConstants.API_STUDENT_EXAM_SHIFT)
@RequiredArgsConstructor
public class HomeRestController {

    private final StudentExamShiftService studentExamShiftService;

    @PostMapping("/join")
    public ResponseEntity<?> joinExamShift(@RequestBody StudentExamShiftRequest studentExamShiftRequest) {
        return Helper.createResponseEntity(studentExamShiftService.joinExamShift(studentExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(studentExamShiftService.getExamShiftByCode(examShiftCode));
    }

    @GetMapping("/path")
    public ResponseEntity<?> getPathByExamShiftCode(@RequestParam(name = "examShiftCode") String examShiftCode) {
        ResponseObject<?> responseObject = studentExamShiftService.getPathByExamShiftCode(examShiftCode);
        return Helper.createResponseEntity(responseObject);
    }

    @GetMapping("/file")
    public ResponseEntity<?> getExamShiftPaperByExamShiftCode(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = studentExamShiftService.getExamShiftPaperByExamShiftCode(fileId);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            FileResourceResponse fileResponse = (FileResourceResponse) responseObject.getData();

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header("Content-Disposition", "inline; filename=" + fileResponse.getFileName())
                    .body(fileResponse.getData());
        }
        return Helper.createResponseEntity(responseObject);
    }

}
