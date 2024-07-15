package fplhn.udpm.examdistribution.core.headsubject.examapproval.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.service.EAExamPaperService;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_EXAM_APPROVAL)
@RequiredArgsConstructor
public class EAExamPaperRestController {

    private final EAExamPaperService hsExamApprovalService;

    @GetMapping
    public ResponseEntity<?> getExamApprovals(final EAExamPaperRequest request) {
        return Helper.createResponseEntity(hsExamApprovalService.getExamApprovals(request));
    }

    @GetMapping("/subject/{departmentFacilityId}")
    public ResponseEntity<?> getSubject(@PathVariable("departmentFacilityId") String departmentFacilityId,
                                        @RequestParam("staffId") String staffId) {
        return Helper.createResponseEntity(hsExamApprovalService.getSubjects(departmentFacilityId, staffId));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(value = "path", defaultValue = "0") String path) throws IOException {
        ResponseObject<?> responseObject = hsExamApprovalService.getFile(path);
        if (responseObject.getStatus().equals(HttpStatus.BAD_REQUEST)) {
            return Helper.createResponseEntity(responseObject);
        } else {
            FileResponse fileResponse = (FileResponse) responseObject.getData();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + fileResponse.getFileName() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(fileResponse.getData());
        }
    }

    @PutMapping
    public ResponseEntity<?> approval(@RequestParam(value = "examPaperId", defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(hsExamApprovalService.approvalExam(examPaperId));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExamPaper(@RequestParam(value = "examPaperId", defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(hsExamApprovalService.deleteExamPaper(examPaperId));
    }

}
