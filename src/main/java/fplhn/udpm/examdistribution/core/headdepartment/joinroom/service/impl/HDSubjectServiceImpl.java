package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDSSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDSubjectService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HDSubjectServiceImpl implements HDSubjectService {

    private final HDSSubjectExtendRepository hdsSubjectExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        try {
            return new ResponseObject<>(
                    hdsSubjectExtendRepository.findAllByClassSubjectCode(
                            classSubjectCode, sessionHelper.getCurrentBlockId()),
                    HttpStatus.OK,
                    "Lấy danh sách môn học thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách môn học: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.OK, "Lỗi khi lấy danh sách môn học!"
            );
        }
    }
}
