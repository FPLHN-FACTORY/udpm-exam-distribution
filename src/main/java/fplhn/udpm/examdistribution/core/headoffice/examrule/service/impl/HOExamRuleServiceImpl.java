package fplhn.udpm.examdistribution.core.headoffice.examrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOCreateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOUpdateUploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.repository.HOExamRuleExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.examrule.repository.HOSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.examrule.service.HOExamRuleService;
import fplhn.udpm.examdistribution.entity.ExamRule;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.GoogleDriveConstant;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class HOExamRuleServiceImpl implements HOExamRuleService {

    private static final Logger log = LoggerFactory.getLogger(HOExamRuleServiceImpl.class);

    private final HOExamRuleExtendRepository examRuleRepository;

    private final HOSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getAllExamRule(HOFindExamRuleRequest request) {
        try {
            Pageable pageable = Helper.createPageable(request, "createdDate");
            return new ResponseObject<>(
                    PageableObject.of(examRuleRepository.getAllExamRule(
                            pageable, request)
                    ),
                    HttpStatus.OK,
                    "Lấy thành công danh sách quy định thi"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Lấy không thành công danh sách quy định thi"
            );
        }
    }

    @Override
    public ResponseObject<?> createExamRule(@Valid HOCreateUploadExamRuleRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Nội quy phòng thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
                );
            }

            String folderName = "ExamRule/" + request.getName();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            ExamRule examRule = new ExamRule();
            examRule.setName(request.getName());
            examRule.setStatus(EntityStatus.ACTIVE);
            examRule.setFileId(googleDriveFileDTO.getId());
            examRuleRepository.save(examRule);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Tải nội quy thi thành công"
            );
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Có lỗi trong quá trình xử lý"
            );
        }
    }

    @Override
    public ResponseObject<?> getFile(String id) {
        try {
            Optional<ExamRule> examRuleOptional = examRuleRepository.findById(id);
            if (examRuleOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy nội quy này!"
                );
            }

            ExamRule examRule = examRuleOptional.get();
            if (examRule.getFileId() == null) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Quy định này chưa có quy định thi"
                );
            }

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_EXAM_RULE + id;

            Object redisValue = redisService.get(redisKey);
            if (redisValue != null) {
                return new ResponseObject<>(
                        new FileResponse(redisValue.toString(), "fileName"),
                        HttpStatus.OK,
                        "success"
                );
            }
            Resource fileResponse = googleDriveFileService.loadFile(examRuleOptional.get().getFileId());
            String data = Base64.getEncoder().encodeToString(fileResponse.getContentAsByteArray());
            redisService.set(redisKey, data);

            return new ResponseObject<>(
                    new FileResponse(data, fileResponse.getFilename()),
                    HttpStatus.OK,
                    "success"
            );
        } catch (IOException e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Quy định thi không tồn tại"
            );
        }

    }

    @Override
    public ResponseObject<?> getListSubject(HOFindSubjectRequest request) {
        try {
            Pageable pageable = Helper.createPageable(request, "createdDate");
            return new ResponseObject<>(
                    PageableObject.of(subjectRepository.getListSubject(
                            pageable,
                            sessionHelper.getCurrentUserId(),
                            sessionHelper.getCurrentUserDepartmentFacilityId(),
                            sessionHelper.getCurrentSemesterId(),
                            request
                    )),
                    HttpStatus.OK,
                    "Lấy thành công danh sách môn học"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy không thành công danh sách môn học"
            );
        }
    }

    @Override
    public ResponseObject<?> chooseExamRule(HOChooseExamRuleRequest request) {
        Optional<ExamRule> examRuleOptional = examRuleRepository.findById(request.getExamRuleId());
        if (examRuleOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy quy định thi này"
            );
        }

        Optional<Subject> subjectOptional = subjectRepository.findById(request.getSubjectId());
        if (subjectOptional.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.NOT_FOUND,
                    "Không tìm thấy môn học này"
            );
        }

        Subject subject = subjectOptional.get();

        if (subjectOptional.get().getExamRule() == null ||
            !subjectOptional.get().getExamRule().getId().equalsIgnoreCase(request.getExamRuleId())) {
            subject.setExamRule(examRuleOptional.get());
            subjectRepository.save(subject);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật quy định thi cho môn học " + subject.getName() + " thành công"
            );
        }

        if (subjectOptional.get().getExamRule().getId().equalsIgnoreCase(request.getExamRuleId())) {
            subject.setExamRule(null);
            subjectRepository.save(subject);
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật quy định thi cho môn học " + subject.getName() + " thành công"
            );
        }


        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Cập nhật quy định thi cho môn học " + subject.getName() + " thành công"
        );
    }

    @Override
    public ResponseObject<?> updateExamRule(HOUpdateUploadExamRuleRequest request) {
        try {
            Optional<ExamRule> examRuleOptional = examRuleRepository.findById(request.getId());
            if (examRuleOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        "Không tìm thấy nội quy phòng thi này"
                );
            }

            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Nội quy phòng thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > GoogleDriveConstant.MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        GoogleDriveConstant.MAX_FILE_SIZE_MESSAGE
                );
            }

            String folderName = "ExamRule/" + request.getName();

            GoogleDriveFileDTO googleDriveFileDTO = googleDriveFileService.upload(request.getFile(), folderName, true);

            googleDriveFileService.deleteById(examRuleOptional.get().getFileId());

            ExamRule examRule = examRuleOptional.get();
            examRule.setName(request.getName());
            examRule.setFileId(googleDriveFileDTO.getId());
            examRuleRepository.save(examRule);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật nội quy thi thành công"
            );
        } catch (Exception e) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Cập nhật nội quy thi không thành công"
            );
        }
    }

};
