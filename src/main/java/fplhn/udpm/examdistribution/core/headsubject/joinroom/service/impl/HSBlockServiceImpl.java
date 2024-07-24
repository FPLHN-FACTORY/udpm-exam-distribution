package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSBlockServiceImpl implements HSBlockService {

    private final HSBlockExtendRepository hsBlockExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                hsBlockExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách block thành công!"
        );
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return new ResponseObject<>(
                hsBlockExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy id block thành công!"
        );
    }
}
