package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.CreateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.UpdateExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository.UEPUploadExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.service.UploadExamPaperService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.email.service.EmailService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class UploadExamPaperServiceImpl implements UploadExamPaperService {

    private final UEPUploadExamPaperExtendRepository examPaperRepository;

    private final UEPMajorFacilityExtendRepository majorFacilityRepository;

    private final UEPSubjectExtendRepository subjectRepository;

    private final UEPStaffExtendRepository staffRepository;

    private final UEPSemesterExtendRepository semesterRepository;

    private final UEPBlockExtendRepository blockRepository;

    private final UEPClassSubjectExtendRepository classSubjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final EmailService emailService;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getListSubject(String semesterId) {
        String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();

        return new ResponseObject<>(
                subjectRepository.getListSubject(userId, semesterId),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công"
        );
    }

    @Override
    public ResponseObject<?> getListCurrentSubject() {
        String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();

        String semesterId = httpSession.getAttribute(SessionConstant.CURRENT_SEMESTER_ID).toString();

        return new ResponseObject<>(
                subjectRepository.getListSubject(userId, semesterId),
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
        String departmentFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();
        return new ResponseObject<>(
                staffRepository.getListStaff(departmentFacilityId),
                HttpStatus.OK,
                "Lấy danh sách nhân viên thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllExamPaper(ListExamPaperRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        return new ResponseObject<>(
                PageableObject.of(examPaperRepository.getListExamPaper(pageable, request, userId, ExamPaperStatus.WAITING_APPROVAL.toString())),
                HttpStatus.OK,
                "Lấy thành công danh sách đề thi"
        );
    }

    @Override
    public ResponseObject<?> getFile(String fileId) {
        try {
            if (fileId.trim().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy file"
                );
            }

            return new ResponseObject<>(
                    googleDriveFileService.loadFile(fileId),
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (IOException e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đã có 1 vài lỗi xảy ra"
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
    public ResponseObject<?> getListMajorFacility() {
        try {
            String facilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString();
            String departmentId = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_ID).toString();
            return new ResponseObject<>(
                    examPaperRepository.getMajorFacilityByDepartmentFacilityId(facilityId, departmentId),
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
    public ResponseObject<?> createExamPaper(@Valid CreateExamPaperRequest request) {
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
                        "Nội quy thi không được lớn hơn 5MB"
                );
            }

            Optional<MajorFacility> isMajorFacilityExist = majorFacilityRepository.findById(request.getMajorFacilityId());
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

            Long now = new Date().getTime();
            boolean isFoundBlock = false;
            for (Block block : blockRepository.findAll()) {
                if (block.getStartTime() <= now && now <= block.getEndTime()) {
                    putExamPaper.setBlock(block);
                    isFoundBlock = true;
                    break;
                }
            }

            if (!isFoundBlock) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy học kỳ block phù hợp"
                );
            }

            String userId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
            String folderName = "Exam/" + isSubjectExist.get().getSubjectCode() + "/" + request.getExamPaperType();
            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

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
    public ResponseObject<?> updateExamPaper(@Valid UpdateExamPaperRequest request) {
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
                        "Nội quy thi không được lớn hơn 5MB"
                );
            }

            Optional<MajorFacility> isMajorFacilityExist = majorFacilityRepository.findById(request.getMajorFacilityId());
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

            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            String subjectId = optionalExamPaper.get().getSubject().getId();
            String departmentFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();
            String[] listEmailStaff = classSubjectRepository.getEmailStaffByBlockId(blockId, subjectId, departmentFacilityId);

            if (listEmailStaff.length == 0) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Môn học này chưa được xếp lớp học"
                );
            }
            emailService.sendEmailPublicMockExamPaper(listEmailStaff);

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

}
