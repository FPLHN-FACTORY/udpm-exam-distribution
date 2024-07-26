package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSClassSubjectResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSClassSubjectService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSClassSubjectServiceImpl implements HSClassSubjectService {

    private final HSClassSubjectExtendRepository hsClassSubjectExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> getClassSubject(String classSubjectCode) {
        try {
            Optional<HSClassSubjectResponse> existingClassSubject = hsClassSubjectExtendRepository.getClassSubject(
                    classSubjectCode,
                    sessionHelper.getCurrentUserDepartmentFacilityId(),
                    sessionHelper.getCurrentSemesterId()
            );

            if (existingClassSubject.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.NOT_FOUND,
                        "Mã lớp môn không tồn tại hoặc không thuộc bộ môn này!"
                );
            }

            return new ResponseObject<>(
                    existingClassSubject.get(),
                    HttpStatus.OK,
                    "Lấy lớp môn thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy lớp môn: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.OK, "Lỗi khi lấy lớp môn!"
            );
        }
    }

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest) {
        try {
            return new ResponseObject<>(
                    hsClassSubjectExtendRepository.getClassSubjectIdByRequest(hsClassSubjectRequest),
                    HttpStatus.OK,
                    "Lấy id lớp môn thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy id lớp môn: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.OK, "Lỗi khi lấy id lớp môn!"
            );
        }
    }

}
