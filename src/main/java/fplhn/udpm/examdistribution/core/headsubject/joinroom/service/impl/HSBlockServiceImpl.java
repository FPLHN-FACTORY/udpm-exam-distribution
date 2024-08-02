package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSBlockService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSBlockServiceImpl implements HSBlockService {

    private final HSBlockExtendRepository hsBlockExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        try {
            return new ResponseObject<>(
                    hsBlockExtendRepository.findAllByClassSubjectCodeAndSubjectId(
                            classSubjectCode, subjectId, sessionHelper.getCurrentSemesterId()),
                    HttpStatus.OK,
                    "Lấy danh sách block thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách block: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.OK, "Lỗi khi lấy danh sách block!"
            );
        }
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        try {
            return new ResponseObject<>(
                    hsBlockExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
                    HttpStatus.OK,
                    "Lấy id block thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy id block: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy id block!"
            );
        }
    }
}
