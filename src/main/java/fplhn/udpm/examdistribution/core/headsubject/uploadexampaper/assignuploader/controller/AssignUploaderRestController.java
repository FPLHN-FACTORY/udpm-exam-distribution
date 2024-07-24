package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.CreateSampleExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_ASSIGN_UPLOADER)
@RequiredArgsConstructor
public class AssignUploaderRestController {

    private final AssignUploaderService assignUploaderService;

    @GetMapping("/subject/{departmentFacilityId}")
    public ResponseEntity<?> getAllSubject(@PathVariable String departmentFacilityId, FindSubjectRequest request) {
        return Helper.createResponseEntity(assignUploaderService.getAllSubject(departmentFacilityId, request));
    }

    @GetMapping("/staff")
    public ResponseEntity<?> getAllStaff(FindStaffRequest request) {
        return Helper.createResponseEntity(assignUploaderService.getAllStaff(request));
    }

    @PostMapping("/assign-uploader")
    public ResponseEntity<?> addOrDelAssignUploader(@RequestBody AssignUploaderRequest request) {
        return Helper.createResponseEntity(assignUploaderService.addOrDelAssignUploader(request));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = assignUploaderService.getFile(fileId);
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

    @PostMapping("/sample-exam-paper")
    public ResponseEntity<?> createSampleExamPaper(@ModelAttribute CreateSampleExamPaperRequest request) {
        return Helper.createResponseEntity(assignUploaderService.createSampleExamPaper(request));
    }

}
