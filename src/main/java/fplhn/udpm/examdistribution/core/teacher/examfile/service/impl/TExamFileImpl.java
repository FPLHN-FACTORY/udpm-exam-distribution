package fplhn.udpm.examdistribution.core.teacher.examfile.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.*;
import fplhn.udpm.examdistribution.core.teacher.examfile.service.TExamFileService;
import fplhn.udpm.examdistribution.entity.*;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TExamFileImpl implements TExamFileService {

    private final TSubjectRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final TMajorFacilityRepository majorFacilityRepository;

    private final TAssignUploaderRepository assignUploaderRepository;

    private final TStaffRepository tStaffRepository;

    private final HttpSession httpSession;

    private final TExamPaperRepository tExamPaperRepository;

    @Override
    public ResponseObject<?> getAllSubject(String departmentFacilityId, TFindSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(pageable, departmentFacilityId, request, (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID))),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> uploadExamRule(String subjectId, TUploadExamFileRequest request) {

        if (request.getFile().isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Đề thi chưa được tải"
            );
        }

        List<AssignUploader>assignUploaders = assignUploaderRepository.findAllBySubject_IdAndStaff_Id(subjectId,(String)httpSession.getAttribute(SessionConstant.CURRENT_USER_ID));
        if (assignUploaders.isEmpty() ||tExamPaperRepository.getCountUploaded(httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString(), subjectId) >= assignUploaders.get(0).getMaxUpload()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Số lượng đề được upload đã đạt tối đa"
            );
        }

        Optional<MajorFacility> majorFacility = majorFacilityRepository.findById(request.getMajorFacilityId());

        Optional<Staff> staffs = tStaffRepository.findById((String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID));

        Optional<Subject> subject = subjectRepository.findById(subjectId);

        System.out.println(request.getMajorFacilityId() + subjectId);
        if (subject.isEmpty() || majorFacility.isEmpty() || staffs.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Không tìm thấy chuyên ngành - cơ sở hoặc môn học hoặc nhân viên"
            );
        }

        String folderName = "Phân công/" + staffs.get().getStaffCode() + " - " + subject.get().getName() + "/" + request.getFolderName() + "/" + request.getExamPaperType();
        GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

        String fileId = googleDriveFileDTO.getId();

        ExamPaper examPaper = new ExamPaper();
        examPaper.setId(CodeGenerator.generateRandomCode());
        examPaper.setPath(fileId);
        examPaper.setExamPaperType(ExamPaperType.valueOf(request.getExamPaperType()));
        examPaper.setExamPaperStatus(ExamPaperStatus.WAITING_APPROVAL);
        examPaper.setMajorFacility(majorFacility.get());
        examPaper.setSubject(subject.get());
        examPaper.setExamPaperCode(subject.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3));
        examPaper.setExamPaperCreatedDate(new Date().getTime());
        examPaper.setStaffUpload(staffs.get());
        if (ExamPaperType.valueOf(request.getExamPaperType()) == ExamPaperType.MOCK_EXAM_PAPER) {
            examPaper.setIsPublic(false);
        } else {
            examPaper.setIsPublic(true);
        }
        examPaper.setStatus(EntityStatus.ACTIVE);
        tExamPaperRepository.save(examPaper);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Tải đề thi thành công"
        );
    }

    @Override
    public ResponseObject<?> getMajorFacilityByDepartmentFacility(String departmentFacilityId) {
        return new ResponseObject<>(majorFacilityRepository.getMajorFacilityByIdFacility(departmentFacilityId), HttpStatus.OK, "Lấy danh sách chuyên ngành theo cơ sở thành công!");
    }

}
