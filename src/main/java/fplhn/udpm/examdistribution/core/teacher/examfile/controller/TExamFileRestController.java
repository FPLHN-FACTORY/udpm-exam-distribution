package fplhn.udpm.examdistribution.core.teacher.examfile.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.service.TExamFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_TEACHER_EXAM_FILE)
@RequiredArgsConstructor
public class TExamFileRestController {

    private final TExamFileService examFileService;

    @GetMapping("/subject/{departmentFacilityId}")
    public ResponseEntity<?> getAllSubject(@PathVariable String departmentFacilityId, TFindSubjectRequest request) {
        return Helper.createResponseEntity(examFileService.getAllSubject(departmentFacilityId, request));
    }

    @PostMapping("/upload/{subjectId}")
    public ResponseEntity<?> uploadExamRule(@PathVariable String subjectId, @ModelAttribute TUploadExamFileRequest request) {
        return Helper.createResponseEntity(examFileService.uploadExamRule(subjectId, request));
    }

    @GetMapping("/major-facility")
    public ResponseEntity<?> getMajorFacility(@RequestParam(value = "majorFacilityId", defaultValue = "0") String majorFacilityId) {
        return Helper.createResponseEntity(examFileService.getMajorFacilityByDepartmentFacility(majorFacilityId));
    }

    @GetMapping("/sample-exam-paper/{subjectId}")
    public ResponseEntity<?> getSampleExamPaper(@PathVariable(value = "subjectId") String subjectId) {
        ResponseObject<?>  responseObject = examFileService.getSampleExamPaper(subjectId);
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

}
