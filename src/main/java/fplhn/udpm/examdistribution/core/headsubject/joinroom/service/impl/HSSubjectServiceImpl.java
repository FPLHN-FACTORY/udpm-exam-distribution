package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSSubjectService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSSubjectServiceImpl implements HSSubjectService {

    private static final Logger log = LoggerFactory.getLogger(HSSubjectServiceImpl.class);
    private final HSSubjectExtendRepository hsSubjectExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        try {
            return new ResponseObject<>(
                    hsSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode),
                    HttpStatus.OK,
                    "Lấy danh sách môn học thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách môn học: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách môn học!"
            );
        }
    }
}
