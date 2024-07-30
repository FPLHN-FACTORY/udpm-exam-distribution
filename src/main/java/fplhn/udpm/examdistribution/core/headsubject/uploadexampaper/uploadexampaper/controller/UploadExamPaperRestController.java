package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListStaffBySubjectIdRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.PublicMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.SharePermissionExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER)
@RequiredArgsConstructor
public class UploadExamPaperRestController {

    private final UploadExamPaperService uploadExamPaperService;

    @GetMapping("/current-subject")
    public ResponseEntity<?> getListCurrentSubject() {
        return Helper.createResponseEntity(uploadExamPaperService.getListCurrentSubject());
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getListStaff() {
        return Helper.createResponseEntity(uploadExamPaperService.getListStaff());
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = uploadExamPaperService.getFile(fileId);
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

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExamPaper(@PathVariable String id) {
        return Helper.createResponseEntity(uploadExamPaperService.deleteExamPaper(id));
    }

    @GetMapping("/exam-paper")
    public ResponseEntity<?> getListExamPaper(ListExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.getAllExamPaper(request));
    }

    @PostMapping("/exam-paper")
    public ResponseEntity<?> createExamPaper(@ModelAttribute CreateExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.createExamPaper(request));
    }

    @PutMapping("/exam-paper")
    public ResponseEntity<?> updateExamPaper(@ModelAttribute UpdateExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.updateExamPaper(request));
    }

    @PostMapping("/send-email-public-exam-paper/{examPaperId}")
    public ResponseEntity<?> sendEmailPublicExamPaper(@PathVariable String examPaperId) {
        return Helper.createResponseEntity(uploadExamPaperService.sendEmailPublicMockExamPaper(examPaperId));
    }

    @PostMapping("/public-mock-exam-paper")
    public ResponseEntity<?> publicMockExamPaper(@RequestBody PublicMockExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.sendEmailPublicMockExamPaper(request));
    }

    @GetMapping("/staffs/{subjectId}")
    public ResponseEntity<?> getListStaffBySubjectId(@PathVariable String subjectId, ListStaffBySubjectIdRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.getListStaffBySubjectId(subjectId, request));
    }

    @PostMapping("/share-permission-exam-paper")
    public ResponseEntity<?> sharePermissionExamPaper(@RequestBody SharePermissionExamPaperRequest request) {
        return Helper.createResponseEntity(uploadExamPaperService.sharePermissionExamPaper(request));
    }

}
