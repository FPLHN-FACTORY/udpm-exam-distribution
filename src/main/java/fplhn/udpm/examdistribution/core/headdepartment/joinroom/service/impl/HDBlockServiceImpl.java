package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDBlockExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDBlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDBlockServiceImpl implements HDBlockService {

    private final HDBlockExtendRepository hdBlockExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                hdBlockExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách block thành công!"
        );
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return new ResponseObject<>(
                hdBlockExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy id block thành công!"
        );
    }
}
