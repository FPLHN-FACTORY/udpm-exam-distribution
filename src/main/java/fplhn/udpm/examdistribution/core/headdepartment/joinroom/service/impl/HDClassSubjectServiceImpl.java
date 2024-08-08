package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDClassSubjectResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDClassSubjectService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class HDClassSubjectServiceImpl implements HDClassSubjectService {

    private static final Logger log = LoggerFactory.getLogger(HDClassSubjectServiceImpl.class);
    private final HDClassSubjectExtendRepository hdClassSubjectExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getClassSubject(String classSubjectCode, String subjectId) {
        try {
            Optional<HDClassSubjectResponse> existingClassSubject = hdClassSubjectExtendRepository.getClassSubject(
                    classSubjectCode, subjectId,
                    sessionHelper.getCurrentUserDepartmentFacilityId(),
                    sessionHelper.getCurrentBlockId());

            if (existingClassSubject.isEmpty()) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND,
                        "Mã lớp môn không tồn tại hoặc không thuộc bộ môn này!");
            }

            return new ResponseObject<>(
                    existingClassSubject.get(),
                    HttpStatus.OK,
                    "Lấy lớp môn thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy lớp môn: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy lớp môn!"
            );
        }
    }

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(HDClassSubjectRequest hdClassSubjectRequest) {
        try {
            return new ResponseObject<>(
                    hdClassSubjectExtendRepository.getClassSubjectIdByRequest(hdClassSubjectRequest),
                    HttpStatus.OK,
                    "Lấy id lớp môn thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy id lớp môn: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy lớp môn!"
            );
        }
    }
}
