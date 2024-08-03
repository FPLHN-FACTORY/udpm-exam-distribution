package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSFacilityChildService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSFacilityChildServiceImpl implements HSFacilityChildService {

    private final HSFacilityChildExtendRepository hsFacilityChildExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        try {
            return new ResponseObject<>(
                    hsFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(
                            classSubjectCode, subjectId, sessionHelper.getCurrentBlockId()),
                    HttpStatus.OK,
                    "Lấy danh sách campus thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách campus: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách campus!"
            );
        }
    }
}
