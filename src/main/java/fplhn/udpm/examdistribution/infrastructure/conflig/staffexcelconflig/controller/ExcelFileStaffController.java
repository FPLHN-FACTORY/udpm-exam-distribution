package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.controller;

import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.jobconflig.ExcelFileToDatabaseJobLauncher;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service.ExcelFileStaffService;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.service.UploadStaffService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.apache.poi.util.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping(MappingConstants.API_HEAD_OFFICE_STAFF)
public class ExcelFileStaffController {

    private final UploadStaffService storageService;

    private final ExcelFileStaffService excelFileStaffService;

    private final ExcelFileToDatabaseJobLauncher excelFileToDatabaseJobLauncher;

    public ExcelFileStaffController(UploadStaffService storageService, ExcelFileStaffService excelFileStaffService, ExcelFileToDatabaseJobLauncher excelFileToDatabaseJobLauncher) {
        this.storageService = storageService;
        this.excelFileStaffService = excelFileStaffService;
        this.excelFileToDatabaseJobLauncher = excelFileToDatabaseJobLauncher;
    }

    @GetMapping(value = "/download-template-staffs")
    public ResponseEntity<?> downloadTemplate() throws IOException {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(new MediaType("application", "force-download"));
        header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=template.xlsx");
        ByteArrayInputStream byteArrayInputStream = excelFileStaffService.downloadTemplate().getData();
        return new ResponseEntity<>(IOUtils.toByteArray(byteArrayInputStream), header, HttpStatus.CREATED);
    }

    @PostMapping("/file/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            Optional<String> extension = storageService.getExtensionByStringHandling(file.getOriginalFilename()).getData();
            if (extension.isEmpty() || !extension.get().equals("xlsx")) {
                message = "File Không Đúng Định Dạng";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
            storageService.init();
            String fileName = storageService.save(file);
            message = "Tải File Excel Thành Công: " + file.getOriginalFilename();
            excelFileToDatabaseJobLauncher.setFullPathFileName(fileName);
            excelFileToDatabaseJobLauncher.enable();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            e.printStackTrace();
            message = "Đã Xảy Ra Lỗi: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

}
