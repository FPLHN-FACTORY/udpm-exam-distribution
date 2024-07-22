package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.controller;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.infrastructure.config.job.examshift.jobconfig.ExamShiftJobLauncher;
import fplhn.udpm.examdistribution.infrastructure.config.upload.FileUploadService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_FILE)
public class ExamShiftFileController {

    @Setter(onMethod_ = @Autowired, onParam_ = @Qualifier("fileUploadShiftServiceImpl"))
    private FileUploadService fileUploadService;

    @Setter(onMethod_ = @Autowired)
    private ExamShiftJobLauncher examShiftJobLauncher;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String message = "";
        try {
            Optional<String> extension = getExtensionByStringHandling(file.getOriginalFilename()).getData();
            if (extension.isEmpty() || !extension.get().equals("xlsx")) {
                message = "File Không Đúng Định Dạng";
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
            }
            fileUploadService.init();
            String fileName = fileUploadService.save(file);
            message = "Tải File Excel Thành Công: " + file.getOriginalFilename();
            examShiftJobLauncher.setFilePathExamShift(fileName);
            examShiftJobLauncher.enable();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            message = "Đã Xảy Ra Lỗi: " + file.getOriginalFilename() + ". Error: " + e.getMessage();
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }

    private ResponseObject<Optional<String>> getExtensionByStringHandling(String filename) {
        if (filename == null || filename.isEmpty()) {
            return ResponseObject.errorForward("File name is empty", HttpStatus.BAD_REQUEST);
        }
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex == -1) {
            return ResponseObject.errorForward("File name does not contain extension", HttpStatus.BAD_REQUEST);
        }
        return new ResponseObject<>(
                Optional.of(filename.substring(dotIndex + 1)),
                HttpStatus.OK,
                "Get extension successfully"
        );
    }

}
