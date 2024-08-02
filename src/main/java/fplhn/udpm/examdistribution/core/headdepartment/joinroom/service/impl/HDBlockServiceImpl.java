package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDBlockService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HDBlockServiceImpl implements HDBlockService {

    private final HDBlockExtendRepository hdBlockExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        try {
            return new ResponseObject<>(
                    hdBlockExtendRepository.findAllByClassSubjectCodeAndSubjectId(
                            classSubjectCode, subjectId, sessionHelper.getCurrentSemesterId()),
                    HttpStatus.OK,
                    "Lấy danh sách block thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách block: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách block!"
            );
        }
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        try {
            return new ResponseObject<>(
                    hdBlockExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
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
