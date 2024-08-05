package fplhn.udpm.examdistribution.core.headsubject.subjectrule.service.impl;

import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRChooseExamRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRUpdateExamTimeRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.response.SRExamTimeResponse;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRExamRuleExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRExamTimeBySubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository.SRSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.service.SRSubjectRuleService;
import fplhn.udpm.examdistribution.entity.ExamRule;
import fplhn.udpm.examdistribution.entity.ExamTimeBySubject;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
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
public class SRSubjectRuleServiceImpl implements SRSubjectRuleService {

    private static final Logger log = LoggerFactory.getLogger(SRSubjectRuleServiceImpl.class);

    private final SRExamRuleExtendRepository examRuleRepository;

    private final SRSubjectExtendRepository subjectRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final SRExamTimeBySubjectExtendRepository examTimeBySubjectRepository;

    private final SRFacilityExtendRepository facilityRepository;

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
                            sessionHelper.getCurrentUserFacilityId(),
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
    public ResponseObject<?> getListExamRule(SRFindSubjectRuleRequest request) {
        try {
            Pageable pageable = Helper.createPageable(request, "createdDate");
            return new ResponseObject<>(
                    PageableObject.of(examRuleRepository.getListExamRule(
                            pageable,
                            request
                    )),
                    HttpStatus.OK,
                    "Lấy thành công danh sách nội quy thi"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Lấy không thành công danh sách nội quy thi"
            );
        }
    }

    @Override
    public ResponseObject<?> chooseExamRule(SRChooseExamRuleRequest request) {
        try {
            Optional<ExamRule> examRuleOptional = examRuleRepository.findById(request.getExamRuleId());
            if (examRuleOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy nội quy thi này"
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
            subject.setExamRule(examRuleOptional.get());
            subjectRepository.save(subject);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Chọn nội quy thi thành công cho môn học " + subject.getName()
            );

        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Chọn nội quy thi không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> updateExamTime(SRUpdateExamTimeRequest request) {
        try {
            Optional<Subject> subjectOptional = subjectRepository.findById(request.getSubjectId());
            if (subjectOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            Optional<Facility> facilityOptional = facilityRepository.findById(sessionHelper.getCurrentUserFacilityId());
            if (facilityOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cơ sở này"
                );
            }

            if (request.getExamTime() < 30 || request.getExamTime() > 120) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_ACCEPTABLE,
                        "Số phút thi phải lớn hơn 30 phút và nhỏ hơn 120 phút"
                );
            }

            Optional<ExamTimeBySubject> examTimeBySubjectOptional = examTimeBySubjectRepository.findExamTimeBySubjectIdAndFacilityId(
                    subjectOptional.get().getId(),
                    sessionHelper.getCurrentUserFacilityId()
            );
            if (examTimeBySubjectOptional.isPresent()) {
                ExamTimeBySubject examTimeBySubject = examTimeBySubjectOptional.get();
                examTimeBySubject.setExam_time(request.getExamTime());
                examTimeBySubjectRepository.save(examTimeBySubject);

                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        "Cập nhật thời gian thi thành công cho môn học " + subjectOptional.get().getName()
                );
            }

            ExamTimeBySubject examTimeBySubject = new ExamTimeBySubject();
            examTimeBySubject.setExam_time(request.getExamTime());
            examTimeBySubject.setSubject(subjectOptional.get());
            examTimeBySubject.setFacility(facilityOptional.get());
            examTimeBySubject.setAllowOnline(false);
            examTimeBySubjectRepository.save(examTimeBySubject);
            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    "Cập nhật thời gian thi thành công cho môn học " + subjectOptional.get().getName()
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Cập nhật thời gian thi không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> getExamTime(SRExamTimeRequest request) {
        try {
            Optional<Subject> subjectOptional = subjectRepository.findById(request.getSubjectId());
            if (subjectOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            Optional<ExamTimeBySubject> examTimeBySubjectOptional = examTimeBySubjectRepository.findExamTimeBySubjectIdAndFacilityId(
                    subjectOptional.get().getId(),
                    sessionHelper.getCurrentUserFacilityId()
            );
            return new ResponseObject<>(
                    examTimeBySubjectOptional
                            .map(examTimeBySubject ->
                                    new SRExamTimeResponse(examTimeBySubject.getExam_time())).orElseGet(
                                    () -> new SRExamTimeResponse(0L)
                            ),
                    HttpStatus.OK,
                    "Lấy thành công thời gian thi"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Cập nhật thời gian thi không thành công"
            );
        }
    }

    @Override
    public ResponseObject<?> allowOnlineSubject(String subjectId) {
        try {
            Optional<Subject> subjectOptional = subjectRepository.findById(subjectId);
            if (subjectOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy môn học này"
                );
            }

            Optional<Facility> facilityOptional = facilityRepository.findById(sessionHelper.getCurrentUserFacilityId());
            if (facilityOptional.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Không tìm thấy cơ sở hiện tại"
                );
            }

            Optional<ExamTimeBySubject> examTimeBySubjectOptional = examTimeBySubjectRepository.findExamTimeBySubjectIdAndFacilityId(
                    subjectId,
                    sessionHelper.getCurrentUserFacilityId()
            );
            if (examTimeBySubjectOptional.isEmpty()) {
                ExamTimeBySubject postExamTimeBySubject = new ExamTimeBySubject();
                postExamTimeBySubject.setFacility(facilityOptional.get());
                postExamTimeBySubject.setSubject(subjectOptional.get());
                postExamTimeBySubject.setStatus(EntityStatus.ACTIVE);
                postExamTimeBySubject.setAllowOnline(true);
                postExamTimeBySubject.setExam_time(0L);

                examTimeBySubjectRepository.save(postExamTimeBySubject);
                return new ResponseObject<>(
                        null,
                        HttpStatus.OK,
                        postExamTimeBySubject.isAllowOnline() ?
                                "Đã cập nhật môn học " + postExamTimeBySubject.getSubject().getName() + " là môn được dùng mạng" :
                                "Đã cập nhật môn học " + postExamTimeBySubject.getSubject().getName() + " là môn không được dùng mạng"
                );
            }

            ExamTimeBySubject examTimeBySubject = examTimeBySubjectOptional.get();
            examTimeBySubject.setAllowOnline(!examTimeBySubject.isAllowOnline());
            examTimeBySubjectRepository.save(examTimeBySubject);

            return new ResponseObject<>(
                    null,
                    HttpStatus.OK,
                    examTimeBySubject.isAllowOnline() ?
                            "Đã cập nhật môn học " + examTimeBySubject.getSubject().getName() + " là môn không được dùng mạng" :
                            "Đã cập nhật môn học " + examTimeBySubject.getSubject().getName() + " là môn được dùng mạng"
            );
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Cập nhật thời gian thi không thành công"
            );
        }
    }

};
