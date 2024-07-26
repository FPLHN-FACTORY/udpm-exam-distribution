package fplhn.udpm.examdistribution.core.headsubject.updateexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.model.request.UEPEditFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.repository.UEPExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.updateexampaper.service.UpdateExamPaperService;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateExamPaperServiceImpl implements UpdateExamPaperService {

    private final UEPExamPaperExtendRepository examPaperRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    @Override
    public ResponseEntity<?> convertPdfToDocx(MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Bạn chưa tải lên file PDF", HttpStatus.NOT_FOUND);
        }

        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("application/pdf")) {
            return new ResponseEntity<>("Vui lòng tải lên file PDF", HttpStatus.NOT_ACCEPTABLE);
        }

        if (file.getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
            return new ResponseEntity<>(GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            XWPFDocument doc = new XWPFDocument();

            PDDocument pdfDocument = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();

            String text = stripper.getText(pdfDocument);

            XWPFParagraph p = doc.createParagraph();
            XWPFRun run = p.createRun();
            run.setText(text);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.write(out);
            byte[] docxBytes = out.toByteArray();

            out.close();
            pdfDocument.close();

            return new ResponseEntity<>(docxBytes, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>("Convert file PDF to Docx không thành công", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseObject<?> editFile(UEPEditFileRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Bạn chưa tải lên file"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
                );
            }

            Optional<ExamPaper> optionalExamPaper = examPaperRepository.findById(request.getExamPaperId());
            if (optionalExamPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            }

            ExamPaper putExamPaper = optionalExamPaper.get();

            String oldPath = putExamPaper.getPath();

            String folderName = "Exam/" + putExamPaper.getSubject().getSubjectCode() + "/" + putExamPaper.getExamPaperType();
            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            putExamPaper.setPath(googleDriveFileDTO.getId());

            examPaperRepository.save(putExamPaper);

            googleDriveFileService.deleteById(oldPath);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Chỉnh sửa file thành công"
            );
        } catch (Exception e) {
            log.info(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chỉnh sửa file không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> getFile(String examPaperId) {
        try {
            Optional<ExamPaper> optionalExamPaper = examPaperRepository.findById(examPaperId);
            if (optionalExamPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            }

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER + optionalExamPaper.get().getId();
//            String fileName = googleDriveFileService.getFileName(optionalExamPaper.get().getPath());

            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        redisValue.toString(),
                        HttpStatus.OK,
                        "Tìm thấy file thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(optionalExamPaper.get().getPath());

            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    data,
                    HttpStatus.OK,
                    "Lấy file thành công"
            );
        } catch (IOException e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chỉnh sửa file không thành công"
            );
        }
    }

}
