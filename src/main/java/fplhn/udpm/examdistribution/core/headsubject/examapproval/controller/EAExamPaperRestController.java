package fplhn.udpm.examdistribution.core.headsubject.examapproval.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.service.EAExamPaperService;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.GetFileRequest;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

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
    public ResponseEntity<?> getSubject(@PathVariable("departmentFacilityId")String departmentFacilityId,
                                        @RequestParam("staffId")String staffId) {
        return Helper.createResponseEntity(hsExamApprovalService.getSubjects(departmentFacilityId,staffId));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(value = "path",defaultValue = "0") String path) throws IOException {
        ResponseObject<?> responseObject = hsExamApprovalService.getFile(path);
        if (responseObject.getStatus().equals(HttpStatus.OK)) {
            Resource resource = (Resource) responseObject.getData();
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(data);
        }
        return Helper.createResponseEntity(responseObject);
    }

    @PutMapping
    public ResponseEntity<?> approval(@RequestParam(value = "examPaperId",defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(hsExamApprovalService.approvalExam(examPaperId));
    }

    @DeleteMapping
    public ResponseEntity<?> deleteExamPaper(@RequestParam(value = "examPaperId",defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(hsExamApprovalService.deleteExamPaper(examPaperId));
    }

}
