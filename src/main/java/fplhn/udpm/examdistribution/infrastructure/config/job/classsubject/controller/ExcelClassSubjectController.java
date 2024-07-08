package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.jobconfig.ExcelFileClassSubjectToDatabaseJobLauncher;
import fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.service.ExcelFileClassSubjectService;
import fplhn.udpm.examdistribution.infrastructure.config.upload.FileUploadService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.utils.Helper;
import org.apache.poi.util.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_CLASS_SUBJECT)
public class ExcelClassSubjectController {

    private final ExcelFileClassSubjectService exportExcelClassSubjectService;

    @Autowired
    private FileUploadService storageService;

    @Autowired
    private ExcelFileClassSubjectToDatabaseJobLauncher jobLauncher;

    public ExcelClassSubjectController(ExcelFileClassSubjectService exportExcelClassSubjectService) {
        this.exportExcelClassSubjectService = exportExcelClassSubjectService;
    }

    @GetMapping("/download-template-class-subject")
    public ResponseEntity<?> downloadTemplate(@RequestParam(value = "semester", defaultValue = "SPRING") SemesterName semester,
                                              @RequestParam(value = "year", defaultValue = "0") Integer year) throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.xlsx");
        ResponseObject<ByteArrayInputStream> byteArrayInputStreamResponseObject = exportExcelClassSubjectService.downloadTemplate(semester, year);
        if (byteArrayInputStreamResponseObject.getStatus() != HttpStatus.OK) {
            return Helper.createResponseEntity(byteArrayInputStreamResponseObject);
        }
        ByteArrayInputStream byteArrayInputStream = byteArrayInputStreamResponseObject.getData();
        return new ResponseEntity<>(IOUtils.toByteArray(byteArrayInputStream), header, HttpStatus.CREATED);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            storageService.init();
            String fileName = storageService.save(file);
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            jobLauncher.setFullPathFileName(fileName);
            jobLauncher.enable();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

}
