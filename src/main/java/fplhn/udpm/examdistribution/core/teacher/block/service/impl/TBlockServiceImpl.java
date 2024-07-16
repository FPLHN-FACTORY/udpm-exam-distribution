package fplhn.udpm.examdistribution.core.teacher.block.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.block.repository.TBlockExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.block.service.TBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TBlockServiceImpl implements TBlockService {

    private final TBlockExtendRepository tBlockExtendRepository;

    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                tBlockExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách block thành công!"
        );
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return new ResponseObject<>(
                tBlockExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy id block thành công!"
        );
    }

}
