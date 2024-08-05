package fplhn.udpm.examdistribution.core.teacher.examfile.controller;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.service.TExamFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_TEACHER_EXAM_FILE)
@RequiredArgsConstructor
public class TExamFileRestController {

    private final TExamFileService examFileService;

    @GetMapping("/subject")
    public ResponseEntity<?> getAllSubject(TFindSubjectRequest request) {
        return Helper.createResponseEntity(examFileService.getAllSubject(request));
    }

    @GetMapping("/subject/{subjectId}")
    public ResponseEntity<?> getOneSubject(@PathVariable("subjectId") String subjectId) {
        return Helper.createResponseEntity(examFileService.getSubjectById(subjectId));
    }

    @PostMapping("/upload/{subjectId}")
    public ResponseEntity<?> uploadExamRule(@PathVariable String subjectId, TUploadExamFileRequest request) {
        return Helper.createResponseEntity(examFileService.uploadExamRule(subjectId, request));
    }

    @GetMapping("/major-facility")
    public ResponseEntity<?> getMajorFacility() {
        return Helper.createResponseEntity(examFileService.getMajorFacilityByDepartmentFacility());
    }

    @GetMapping("/sample-exam-paper/{subjectId}")
    public ResponseEntity<?> getSampleExamPaper(@PathVariable(value = "subjectId") String subjectId) {
        ResponseObject<?> responseObject = examFileService.getSampleExamPaper(subjectId);
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

    @GetMapping
    public ResponseEntity<?> getMockExamPapers(TExamFileRequest request) {
        return Helper.createResponseEntity(examFileService.getExamPapers(request));
    }

    @GetMapping("/count/{subjectId}")
    public ResponseEntity<?> getCount(@PathVariable("subjectId") String subjectId) {
        return Helper.createResponseEntity(examFileService.getCount(subjectId));
    }

    @GetMapping("/detail-exam-paper")
    public ResponseEntity<?> getExamPaper(@RequestParam(value = "examPaperId", defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(examFileService.getExamPaper(examPaperId));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteExamPaper(@RequestParam(value = "examPaperId", defaultValue = "0") String examPaperId) {
        return Helper.createResponseEntity(examFileService.deleteExamPaper(examPaperId));
    }

}
