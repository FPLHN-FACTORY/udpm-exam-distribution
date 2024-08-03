package fplhn.udpm.examdistribution.core.headsubject.subjectrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRExamRuleExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.service.SRExamRuleService;
import fplhn.udpm.examdistribution.entity.ExamRule;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.RedisPrefixConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
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
public class SRExamRuleServiceImpl implements SRExamRuleService {

    private static final Logger log = LoggerFactory.getLogger(SRExamRuleServiceImpl.class);

    private final SRExamRuleExtendRepository examRuleRepository;

    private final SRSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    private final SessionHelper sessionHelper;

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
    public ResponseObject<?> getListSubject(SRFindSubjectRequest request) {
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

};
