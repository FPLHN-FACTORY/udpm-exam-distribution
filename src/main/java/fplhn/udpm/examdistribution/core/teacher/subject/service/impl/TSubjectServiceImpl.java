package fplhn.udpm.examdistribution.core.teacher.subject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.subject.repository.TSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.subject.service.TSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TSubjectServiceImpl implements TSubjectService {

    private final TSubjectExtendRepository tSubjectExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        return new ResponseObject<>(
                tSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công!"
        );
    }

}
