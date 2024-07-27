package fplhn.udpm.examdistribution.core.teacher.examfile.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TUploadExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TCountExamPaperByStatus;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TSampleExamPaperResponse;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TSubjectResponse;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TAssignUploaderRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TBlockRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TExamPaperRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TMajorFacilityRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TStaffRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.repository.TSubjectRepository;
import fplhn.udpm.examdistribution.core.teacher.examfile.service.TExamFileService;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMockExamPaperRequest;
import fplhn.udpm.examdistribution.entity.AssignUploader;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.*;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
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

    private final TBlockRepository blockRepository;

    private final RedisService redisService;

    @Override
    public ResponseObject<?> getAllSubject(TFindSubjectRequest request) {
        String departmentFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(subjectRepository.getAllSubject(
                        pageable,
                        departmentFacilityId,
                        request,
                        (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID))),
                HttpStatus.OK,
                "Lấy thành công danh sách môn học"
        );
    }

    @Override
    public ResponseObject<?> getSubjectById(String subjectId) {
        String departmentFacilityId = httpSession.getAttribute(
                SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();

        Optional<TSubjectResponse> response = subjectRepository.getSubjectById(
                departmentFacilityId,
                (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID),
                subjectId);

        if (response.isPresent()) {
            return new ResponseObject<>(
                    response.get(),
                    HttpStatus.OK,
                    "Lấy thành công danh sách môn học"
            );
        }

        return new ResponseObject<>(
                null,
                HttpStatus.BAD_REQUEST,
                "Không tìm thấy môn học"
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

        if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
            );
        }

        String currentUserId = (String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID);
        List<AssignUploader> assignUploader = assignUploaderRepository
                .findAllBySubject_IdAndStaff_Id(subjectId, currentUserId);

        if (assignUploader.isEmpty() || tExamPaperRepository.getCountUploaded(currentUserId, subjectId) >= assignUploader.get(0).getMaxUpload()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_ACCEPTABLE,
                    "Số lượng đề được upload đã đạt tối đa"
            );
        }

        String majorFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_MAJOR_FACILITY_ID).toString();
        Optional<MajorFacility> majorFacility = majorFacilityRepository.findById(majorFacilityId);

        Optional<Staff> staffs = tStaffRepository.findById((String) httpSession.getAttribute(SessionConstant.CURRENT_USER_ID));

        Optional<Subject> subject = subjectRepository.findById(subjectId);

        if (subject.isEmpty() || majorFacility.isEmpty() || staffs.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Không tìm thấy chuyên ngành - cơ sở hoặc môn học hoặc nhân viên"
            );
        }

        String folderName = "Phân công/" + staffs.get().getStaffCode() + " - " + subject.get().getName() + "/" + request.getFolderName();
        GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);
        String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();

        String fileId = googleDriveFileDTO.getId();

        ExamPaper examPaper = new ExamPaper();
        examPaper.setId(CodeGenerator.generateRandomCode());
        examPaper.setPath(fileId);
        examPaper.setExamPaperStatus(ExamPaperStatus.WAITING_APPROVAL);
        examPaper.setMajorFacility(majorFacility.get());
        examPaper.setSubject(subject.get());
        examPaper.setExamPaperCode(subject.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3));
        examPaper.setExamPaperCreatedDate(new Date().getTime());
        examPaper.setStaffUpload(staffs.get());
        examPaper.setBlock(blockRepository.getReferenceById(blockId));
        examPaper.setIsPublic(false);
        examPaper.setStatus(EntityStatus.ACTIVE);
        tExamPaperRepository.save(examPaper);

        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Tải đề thi thành công"
        );
    }

    @Override
    public ResponseObject<?> getMajorFacilityByDepartmentFacility() {
        String departmentFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString();
        return new ResponseObject<>(
                majorFacilityRepository.getMajorFacilityByIdFacility(departmentFacilityId),
                HttpStatus.OK,
                "Lấy danh sách chuyên ngành theo cơ sở thành công!");
    }

    @Override
    public ResponseObject<?> getSampleExamPaper(String subjectId) {
        try {
            List<TSampleExamPaperResponse> examPaper = tExamPaperRepository.getSampleExamPaper(
                    httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_FACILITY_ID).toString(),
                    subjectId);
            if (examPaper.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Không tìm thấy đề thi thử"
                );
            }
            String redisKey = RedisPrefixConstant.REDIS_PREFIX_SAMPLE_EXAM_PAPER + examPaper.get(0).getId();
            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "Tìm thấy đề thi thành công"
                );
            }

            Resource resource = googleDriveFileService.loadFile(examPaper.get(0).getPath());
            String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
            redisService.set(redisKey, data);
            return new ResponseObject<>(
                    new FileResponse(data, resource.getFilename()), HttpStatus.OK, "Lấy đề thi mẫu thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Có lỗi xảy ra khi lấy đề thi thử"
            );
        }
    }

    @Override
    public ResponseObject<?> getExamPapers(TExamFileRequest request) {
        String staffId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                tExamPaperRepository.getExamPapers(pageable,request,staffId),
                HttpStatus.OK,
                "Lấy danh sách đề thi thành công");
    }

    @Override
    public ResponseObject<?> getExamPaper(String examPaperId) {
      try{
          String staffId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
          Optional<String> fileId = tExamPaperRepository.getExamPaper(examPaperId,staffId);
          if (fileId.isEmpty()) {
              return new ResponseObject<>(
                      null,
                      HttpStatus.BAD_REQUEST,
                      "Lấy đề thi thất bại");
          }
          String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_PAPER + examPaperId;
          Object redisValue = redisService.get(redisKey);
          if (redisValue != null) {
              return new ResponseObject<>(
                      new FileResponse(redisValue.toString(), "fileName"),
                      HttpStatus.OK,
                      "Lấy đề thi thành công"
              );
          }
          Resource resource = googleDriveFileService.loadFile(fileId.get());
          String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
          redisService.set(redisKey, data);
          return new ResponseObject<>(
                  new FileResponse(data, resource.getFilename()), HttpStatus.OK, "Lấy đề thi thành công"
          );
      }catch (Exception e){
          e.printStackTrace();
          return new ResponseObject<>(
                  null,
                  HttpStatus.BAD_REQUEST,
                  "Có lỗi xảy ra khi lấy đề thi"
          );
      }
    }

    @Override
    public ResponseObject<?> getCount(String subjectId) {
        String staffId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        Optional<TCountExamPaperByStatus> countExamPaperByStatus = tExamPaperRepository.countExamPaper(subjectId,staffId);
        if (countExamPaperByStatus.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi xảy ra khi lấy số lượng đề thi");
        }
        return new ResponseObject<>(
                countExamPaperByStatus,
                HttpStatus.OK,
                "Lấy số lượng đề thành công");
    }

}
