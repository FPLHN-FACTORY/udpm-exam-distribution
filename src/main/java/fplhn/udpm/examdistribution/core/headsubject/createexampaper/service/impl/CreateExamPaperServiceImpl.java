package fplhn.udpm.examdistribution.core.headsubject.createexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.model.request.CREPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository.CREPBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository.CREPExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository.CREPMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository.CREPStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.repository.CREPSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.createexampaper.service.CreateExamPaperService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class CreateExamPaperServiceImpl implements CreateExamPaperService {

    private final SessionHelper sessionHelper;

    private final CREPSubjectExtendRepository subjectRepository;

    private final CREPMajorFacilityExtendRepository majorFacilityRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final CREPBlockExtendRepository blockRepository;

    private final CREPStaffExtendRepository staffRepository;

    private final CREPExamPaperExtendRepository examPaperRepository;

    @Override
    public ResponseEntity<?> convertPdfToDocx(MultipartFile file) {

        if (file.isEmpty()) {
            return new ResponseEntity<>("Bạn chưa tải lên file PDF", HttpStatus.NOT_FOUND);
        }

        if (!Objects.requireNonNull(file.getContentType()).equalsIgnoreCase("application/pdf")) {
            return new ResponseEntity<>("Vui lòng tải lên file PDF", HttpStatus.NOT_ACCEPTABLE);
        }

        if(file.getSize() > GoogleDriveConstant.MAX_FILE_SIZE){
            return new ResponseEntity<>(GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE, HttpStatus.NOT_ACCEPTABLE);
        }

        try {
            // Tạo tài liệu Word mới
            XWPFDocument doc = new XWPFDocument();

            // Mở file PDF
            PDDocument pdfDocument = PDDocument.load(file.getInputStream());
            PDFTextStripper stripper = new PDFTextStripper();

            // Đọc file PDF từ trang đầu tiên đến trang cuối cùng
            String text = stripper.getText(pdfDocument);

            // Tạo đoạn văn và thêm văn bản vào tài liệu Word
            XWPFParagraph p = doc.createParagraph();
            XWPFRun run = p.createRun();
            run.setText(text);

            // Ghi tài liệu Word ra ByteArrayOutputStream
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
    public ResponseObject<?> getListSubject() {
        String userId = sessionHelper.getCurrentUserId();
        String semesterId = sessionHelper.getCurrentSemesterId();
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        return new ResponseObject<>(
                subjectRepository.getListSubject(userId, departmentFacilityId, semesterId),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getListMajorFacility() {
        try {
            String majorFacilityId = sessionHelper.getCurrentUserMajorFacilityId();
            String semesterId = sessionHelper.getCurrentSemesterId();
            String staffId = sessionHelper.getCurrentUserId();
            return new ResponseObject<>(
                    majorFacilityRepository.getMajorFacilityByDepartmentFacilityId(majorFacilityId, staffId, semesterId),
                    HttpStatus.OK,
                    "Lấy thành công danh sách chuyên ngành cơ sở"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> createExamPaper(@Valid CREPCreateExamPaperRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Đề thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
                );
            }

            Optional<MajorFacility> isMajorFacilityExist = majorFacilityRepository.findById(sessionHelper.getCurrentUserMajorFacilityId());
            if (isMajorFacilityExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chuyên ngành cơ sở này"
                );
            }

            Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
            if (isSubjectExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            ExamPaper putExamPaper = new ExamPaper();

            String blockId = sessionHelper.getCurrentBlockId();
            Optional<Block> isBlockExist = blockRepository.findById(blockId);
            if (isBlockExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy học kỳ block phù hợp"
                );
            }

            String userId = sessionHelper.getCurrentUserId();
            String folderName = "Exam/" + isSubjectExist.get().getSubjectCode() + "/" + request.getExamPaperType();
            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            putExamPaper.setBlock(isBlockExist.get());
            putExamPaper.setPath(googleDriveFileDTO.getId());
            putExamPaper.setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
            putExamPaper.setExamPaperStatus(ExamPaperStatus.IN_USE);
            putExamPaper.setMajorFacility(isMajorFacilityExist.get());
            putExamPaper.setSubject(isSubjectExist.get());
            putExamPaper.setExamPaperCode(isSubjectExist.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3));
            putExamPaper.setExamPaperCreatedDate(new Date().getTime());
            putExamPaper.setStaffUpload(staffRepository.findById(userId).get());
            putExamPaper.setStatus(EntityStatus.ACTIVE);
            putExamPaper.setContentFile(request.getContentFile());
            if (ExamPaperType.valueOf(request.getExamPaperType()).equals(ExamPaperType.MOCK_EXAM_PAPER)) {
                putExamPaper.setIsPublic(false);
            }
            examPaperRepository.save(putExamPaper);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Tải đề thi thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

}
