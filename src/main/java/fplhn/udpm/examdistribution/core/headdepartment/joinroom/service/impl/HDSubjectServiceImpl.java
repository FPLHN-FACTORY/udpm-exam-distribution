package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDSSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDSubjectServiceImpl implements HDSubjectService {

    private final HDSSubjectExtendRepository hdsSubjectExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        return new ResponseObject<>(
                hdsSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công!"
        );
    }
}
