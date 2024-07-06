package fplhn.udpm.examdistribution.core.teacher.block.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.block.repository.BlockTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.block.service.BlockTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlockTeacherServiceImpl implements BlockTeacherService {

    private final BlockTeacherExtendRepository blockTeacherExtendRepository;

    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                blockTeacherExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách block thành công!"
        );
    }

    @Override
    public ResponseObject<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return new ResponseObject<>(
                blockTeacherExtendRepository.findBlockId(examShiftDate, classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy id block thành công!"
        );
    }

}
