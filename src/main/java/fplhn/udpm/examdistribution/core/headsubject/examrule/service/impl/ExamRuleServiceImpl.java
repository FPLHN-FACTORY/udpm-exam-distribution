package fplhn.udpm.examdistribution.core.headsubject.examrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.ChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.UploadExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.FileResponse;
import fplhn.udpm.examdistribution.core.headsubject.examrule.repository.ERExamRuleExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.examrule.repository.ERSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.examrule.service.ExamRuleService;
import fplhn.udpm.examdistribution.entity.ExamRule;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.dto.GoogleDriveFileDTO;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
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
public class ExamRuleServiceImpl implements ExamRuleService {

    private static final Logger log = LoggerFactory.getLogger(ExamRuleServiceImpl.class);

    private final ERExamRuleExtendRepository examRuleRepository;

    private final ERSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final SessionHelper sessionHelper;

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    @Override
    public ResponseObject<?> getAllExamRule(FindExamRuleRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(examRuleRepository.getAllExamRule(
                        pageable, request)
                ),
                HttpStatus.OK,
                "Lấy thành công danh sách quy định thi"
        );
    }

    @Override
    public ResponseObject<?> createExamRule(@Valid UploadExamRuleRequest request) {
        try {
            if (request.getFile().isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Nội quy phòng thi chưa được tải"
                );
            }

            if (request.getFile().getSize() > MAX_FILE_SIZE) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Nội quy phòng thi không được lớn hơn 5MB"
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

            String redisKey = RedisPrefixConstant.REDIS_PREFIX_DETAIL_EXAM_RULE + id;

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
            e.printStackTrace();
            return new ResponseObject<>(
                    "Quy định thi không tồn tại",
                    HttpStatus.OK,
                    "success"
            );
        }

    }

    @Override
    public ResponseObject<?> getListSubject(FindSubjectRequest request) {
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
    public ResponseObject<?> chooseExamRule(ChooseExamRuleRequest request) {
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

};
