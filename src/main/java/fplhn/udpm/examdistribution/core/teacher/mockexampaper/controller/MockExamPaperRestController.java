package fplhn.udpm.examdistribution.core.teacher.mockexampaper.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TSubjectMockExamRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.TMockExamPaperService;
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
@RequestMapping(MappingConstants.API_TEACHER_MOCK_EXAM_PAPER)
@RequiredArgsConstructor
public class MockExamPaperRestController {

    private final TMockExamPaperService mockExamPaperService;

    @GetMapping("/subject")
    public ResponseEntity<?> getSubjects(TSubjectMockExamRequest request) {
        return Helper.createResponseEntity(mockExamPaperService.getAllSubject(request));
    }

    @GetMapping("/semester")
    public ResponseEntity<?> getSemester() {
        return Helper.createResponseEntity(mockExamPaperService.getSemesters());
    }

    @GetMapping
    public ResponseEntity<?> getMockExamPapers(TMockExamPaperRequest request) {
        return Helper.createResponseEntity(mockExamPaperService.getMockExams(request));
    }

    @GetMapping("/file")
    public ResponseEntity<?> getFile(@RequestParam(value = "idMockExamPaper",defaultValue = "0") String idMockExamPaper) throws IOException {
        ResponseObject<?> responseObject = mockExamPaperService.getFile(idMockExamPaper);
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

    @GetMapping("/download")
    public ResponseEntity<?> downLoad(@RequestParam(name = "fileId") String fileId) throws IOException {
        ResponseObject<?> responseObject = mockExamPaperService.downLoad(fileId);
        Resource resource = (Resource) responseObject.getData();
        String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=\"" + resource.getFilename() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(data);
    }

}
