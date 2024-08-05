package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPCreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPGetFileRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPUpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPChooseExamPaperRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository.CEPUploadExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.service.ChooseExamPaperService;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.ExamPaperBySemester;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class ChooseExamPaperServiceImpl implements ChooseExamPaperService {

    private final CEPUploadExamPaperExtendRepository examPaperRepository;

    private final CEPMajorFacilityExtendRepository majorFacilityRepository;

    private final CEPSubjectExtendRepository subjectRepository;

    private final CEPStaffExtendRepository staffRepository;

    private final CEPSemesterExtendRepository semesterRepository;

    private final CEPBlockExtendRepository blockRepository;

    private final CEPClassSubjectExtendRepository classSubjectRepository;

    private final CEPChooseExamPaperRepository chooseExamPaperRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final EmailService emailService;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getListSubject(String semesterId) {
        String userId = sessionHelper.getCurrentUserId();
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        return new ResponseObject<>(
                subjectRepository.getListSubject(userId, departmentFacilityId, semesterId),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getListCurrentSubject() {
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
    public ResponseObject<?> getListSemester() {
        return new ResponseObject<>(
                semesterRepository.getListSemester(),
                HttpStatus.OK,
                "Lấy danh sách học kỳ thành công"
        );
    }

    @Override
    public ResponseObject<?> getListBlock(String semesterId) {
        return new ResponseObject<>(
                blockRepository.getListBlock(semesterId),
                HttpStatus.OK,
                "Lấy danh sách block thành công"
        );
    }

    @Override
    public ResponseObject<?> getListStaff() {
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        return new ResponseObject<>(
                staffRepository.getListStaff(departmentFacilityId),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllExamPaper(CEPListExamPaperRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String userId = sessionHelper.getCurrentUserId();
        String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();
        String semesterId = sessionHelper.getCurrentSemesterId();
        return new ResponseObject<>(
                PageableObject.of(examPaperRepository.getListExamPaper(
                        pageable, request,
                        userId, departmentFacilityId,
                        semesterId, ExamPaperStatus.WAITING_APPROVAL.toString(),
                        ExamPaperType.SAMPLE_EXAM_PAPER.toString())
                ),
                HttpStatus.OK,
                "Lấy thành công danh sách đề thi"
        );
    }

    @Override
    public ResponseObject<?> getFile(CEPGetFileRequest request) {
        try {
            if (request.getFileId().trim().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Bạn chưa tải lên file"
                );
            }

            Optional<ExamPaper> examPaper = examPaperRepository.findByPath(request.getFileId());
            if (examPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy đề thi này"
                );
            }

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER + examPaper.get().getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Tìm thấy đề thi thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(request.getFileId());
            String fileName = googleDriveFileService.getFileName(request.getFileId());

            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);
            return new ResponseObject<>(
                    new FileResponse(data, fileName),
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (IOException e) {
            e.printStackTrace(System.out);
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Không lấy được đề thi"
            );
        }
    }

    @Override
    public ResponseObject<?> deleteExamPaper(String examPaperId) {
        try {
            Optional<ExamPaper> isExamPaperExist = examPaperRepository.findById(examPaperId);
            if (isExamPaperExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
                );
            } else {
                ExamPaper examPaper = isExamPaperExist.get();
                examPaper.setExamPaperStatus(
                        examPaper.getExamPaperStatus().equals(ExamPaperStatus.IN_USE) ? ExamPaperStatus.STOP_USING : ExamPaperStatus.IN_USE
                );
                examPaperRepository.save(examPaper);
                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        "Thay đổi trạng thái của đề thi thành công"
                );
            }
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
            );
        }
    }

    @Override
    public ResponseObject<?> createExamPaper(@Valid CEPCreateExamPaperRequest request) {
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

    @Override
    public ResponseObject<?> updateExamPaper(@Valid CEPUpdateExamPaperRequest request) {
        try {
            Optional<ExamPaper> isExamPaperExist = examPaperRepository.findById(request.getExamPaperId());
            if (isExamPaperExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy đề thi này"
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

            ExamPaper putExamPaper = isExamPaperExist.get();

            String oldFileId = putExamPaper.getPath();
            String folderName = "Exam/" + isSubjectExist.get().getSubjectCode() + "/" + request.getExamPaperType();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);
            putExamPaper.setPath(googleDriveFileDTO.getId());
            googleDriveFileService.deleteById(oldFileId);

            putExamPaper.setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
            if (ExamPaperType.valueOf(request.getExamPaperType()).equals(ExamPaperType.MOCK_EXAM_PAPER)) {
                putExamPaper.setIsPublic(false);
            }
            putExamPaper.setMajorFacility(isMajorFacilityExist.get());
            putExamPaper.setSubject(isSubjectExist.get());
            examPaperRepository.save(putExamPaper);


            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật đề thi thành công"
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
    public ResponseObject<?> sendEmailPublicExamPaper(String examPaperId) {
        try {
            Optional<ExamPaper> optionalExamPaper = examPaperRepository.findById(examPaperId);
            if (optionalExamPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Không tìm thấy đề thi này"
                );
            }

            if (optionalExamPaper.get().getIsPublic()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Đề thi này đã được public"
                );
            }

            String blockId = sessionHelper.getCurrentBlockId();
            String subjectId = optionalExamPaper.get().getSubject().getId();
            String departmentFacilityId = sessionHelper.getCurrentUserDepartmentFacilityId();

            String[] listEmailStaff = classSubjectRepository.getEmailStaffByBlockId(blockId, subjectId, departmentFacilityId);
            if (listEmailStaff.length == 0) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Môn học này chưa được xếp lớp học"
                );
            }
//            emailService.sendEmailPublicMockExamPaper(listEmailStaff);

            ExamPaper examPaper = optionalExamPaper.get();
            examPaper.setIsPublic(true);
            examPaperRepository.save(examPaper);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Đề thi đã được public tới những giảng viên dạy môn " + examPaper.getSubject().getName()
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi xảy ra trong quá trình thực thi"
            );
        }
    }

    @Override
    @Modifying
    @Transactional
    public ResponseObject<?> chooseExamPaper(String examPaperId) {
        Optional<ExamPaper> examPaperOptional = examPaperRepository.findExamPaperById(examPaperId);
        if (examPaperOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy đề thi này"
            );
        }

        String semesterId = sessionHelper.getCurrentSemesterId();

        Optional<Semester> semesterOptional = semesterRepository.findSemesterById(semesterId);
        if (semesterOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy học kỳ"
            );
        }

        String majorFacilityId = sessionHelper.getCurrentUserMajorFacilityId();
        Optional<ExamPaperBySemester> isExamPaperBySemesterExist = chooseExamPaperRepository.findByExamPaperIdAndSemesterId(examPaperId, semesterId, majorFacilityId);

        if (isExamPaperBySemesterExist.isEmpty()) {
            ExamPaperBySemester examPaperBySemester = new ExamPaperBySemester();
            examPaperBySemester.setExamPaper(examPaperOptional.get());
            examPaperBySemester.setStatus(EntityStatus.ACTIVE);
            examPaperBySemester.setSemester(semesterOptional.get());

            chooseExamPaperRepository.save(examPaperBySemester);
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Thêm thành công đề thi vào danh sách đề thi của học kỳ " + semesterOptional.get().getSemesterName()
            );
        }

        chooseExamPaperRepository.deleteById(isExamPaperBySemesterExist.get().getId());
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Xóa thành công đề thi khỏi danh sách đề thi của học kỳ " + semesterOptional.get().getSemesterName()
        );
    }

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
            log.error(e.getMessage());
            return new ResponseEntity<>("Convert file PDF to Docx không thành công", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public ResponseObject<?> getListResource(String examPaperId) {
//        Pageable pageable = Helper.createPageable(request, "createdDate");
//        return new ResponseObject<>(
//                PageableObject.of(),
//                HttpStatus.OK,
//                "Lấy thành công danh sách đề thi"
//        );
        return null;
    }

}
