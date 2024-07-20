package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.AssignUploaderRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.CreateSampleExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUAssignUploaderExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUExamPaperExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUMajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUSemesterExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository.AUSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.service.AssignUploaderService;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AssignUploaderServiceImpl implements AssignUploaderService {

    private final AUSubjectExtendRepository subjectRepository;

    private final AUStaffExtendRepository staffExtendRepository;

    private final AUAssignUploaderExtendRepository assignUploaderRepository;

    private final AUSemesterExtendRepository semesterRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final AUExamPaperExtendRepository examPaperExtendRepository;

    private final AUBlockExtendRepository blockExtendRepository;

    private final AUMajorFacilityExtendRepository majorFacilityExtendRepository;

    private final RedisService redisService;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getAllSubject(String departmentFacilityId, FindSubjectRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String semesterId = sessionHelper.getCurrentSemesterId();
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(pageable, departmentFacilityId, semesterId, request)),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> getAllStaff(String departmentFacilityId, FindStaffRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        String userId = sessionHelper.getCurrentUserId();
        String semesterId = sessionHelper.getCurrentSemesterId();
        return new ResponseObject<>(
                PageableObject.of(staffExtendRepository.getAllStaff(pageable, departmentFacilityId, request, userId, semesterId)),
                HttpStatus.OK,
                "Lấy thành công danh sách nhân viên"
        );
    }

    @Override
    public ResponseObject<?> addOrDelAssignUploader(AssignUploaderRequest request) {
        Optional<AssignUploader> isAssignUploaderExist = assignUploaderRepository.isAssignUploaderExist(request);
        if (isAssignUploaderExist.isPresent()) {
            assignUploaderRepository.delete(isAssignUploaderExist.get());
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Xóa thành công giảng viên " + isAssignUploaderExist.get().getStaff().getName() + " khỏi danh sách người tải đề"
            );
        }

        Optional<Staff> isStaffExist = staffExtendRepository.findById(request.getStaffId());
        if (isStaffExist.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Không tìm thấy nhân viên này"
            );
        }

        Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
        if (isSubjectExist.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Không tìm thấy môn học này"
            );
        }

        String semesterId = sessionHelper.getCurrentSemesterId();
        Optional<ExamPaper> examPaperOptional = examPaperExtendRepository.findSampleExamPaperBySubjectId(isSubjectExist.get().getId(), semesterId);
        if (examPaperOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Môn học này chưa tải đề thi mẫu"
            );
        }

        if (request.getMaxUpload() == null) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Bạn chưa điền số lượng đề tải"
            );
        }

        if (request.getMaxUpload().compareTo(new BigInteger("100")) > 0) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Số lượng đề phân công không được quá 100 đề"
            );
        }

        AssignUploader postAssignUploader = new AssignUploader();
        postAssignUploader.setStaff(isStaffExist.get());
        postAssignUploader.setSubject(isSubjectExist.get());
        postAssignUploader.setStatus(EntityStatus.ACTIVE);
        postAssignUploader.setSemester(semesterRepository.getReferenceById(semesterId));
        postAssignUploader.setMaxUpload(request.getMaxUpload().intValue());

        AssignUploader postedAssignUploader = assignUploaderRepository.save(postAssignUploader);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Phân công giảng viên " + postedAssignUploader.getStaff().getName() + " làm người tải đề cho môn học "
                + postAssignUploader.getSubject().getName() + " thành công"
        );
    }

    @Override
    public ResponseObject<?> getFile(String fileId) {
        try {
            if (fileId.equalsIgnoreCase("null")) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Môn học này chưa có đề thi mẫu"
                );
            }

            Optional<ExamPaper> examPaper = examPaperExtendRepository.findByPath(fileId);

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_SAMPLE_EXAM_PAPER + examPaper.get().getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Tìm thấy file thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(fileId);
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new FileResponse(data, resource.getFilename()),
                    HttpStatus.OK,
                    "Tìm thấy file thành công"
            );
        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Đề thi mẫu không tồn tại"
            );
        }
    }

    @Override
    public ResponseObject<?> createSampleExamPaper(CreateSampleExamPaperRequest request) {
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

            Optional<Subject> isSubjectExist = subjectRepository.findById(request.getSubjectId());
            if (isSubjectExist.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            Optional<MajorFacility> majorFacility = majorFacilityExtendRepository.findById(request.getMajorFacilityId());
            if (majorFacility.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy chuyên ngành cơ sở này"
                );
            }

            String semesterId = sessionHelper.getCurrentSemesterId();
            Optional<ExamPaper> examPaperOptional = examPaperExtendRepository.findSampleExamPaperBySubjectId(isSubjectExist.get().getId(), semesterId);
            if (examPaperOptional.isPresent()) {
                ExamPaper examPaper = examPaperOptional.get();
                googleDriveFileService.deleteById(examPaper.getPath());

                String folderName = "SampleExam/" + isSubjectExist.get().getSubjectCode();
                GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);
                examPaper.setPath(googleDriveFileDTO.getId());
                examPaper.setMajorFacility(majorFacility.get());
                examPaperExtendRepository.save(examPaper);

                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        "Tải đề thi thành công"
                );
            }

            ExamPaper putExamPaper = new ExamPaper();

            Long now = new Date().getTime();
            boolean isFoundBlock = false;
            for (Block block : blockExtendRepository.findAll()) {
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

            String userId = sessionHelper.getCurrentUserId();
            String folderName = "SampleExam/" + isSubjectExist.get().getSubjectCode();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            putExamPaper.setPath(googleDriveFileDTO.getId());
            putExamPaper.setExamPaperType(ExamPaperType.SAMPLE_EXAM_PAPER);
            putExamPaper.setExamPaperStatus(ExamPaperStatus.IN_USE);
            putExamPaper.setSubject(isSubjectExist.get());
            putExamPaper.setExamPaperCode(isSubjectExist.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3));
            putExamPaper.setExamPaperCreatedDate(new Date().getTime());
            putExamPaper.setStaffUpload(staffExtendRepository.findById(userId).get());
            putExamPaper.setStatus(EntityStatus.ACTIVE);
            putExamPaper.setMajorFacility(majorFacility.get());
            putExamPaper.setIsPublic(true);

            examPaperExtendRepository.save(putExamPaper);

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
