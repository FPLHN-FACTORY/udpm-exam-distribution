package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSSubjectServiceImpl implements HSSubjectService {

    private final HSSubjectExtendRepository hsSubjectExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        return new ResponseObject<>(
                hsSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công!"
        );
    }
}
