package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TClassSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TClassSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TClassSubjectServiceImpl implements TClassSubjectService {

    private final TClassSubjectExtendRepository tClassSubjectExtendRepository;

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(TClassSubjectRequest tClassSubjectRequest) {
        return new ResponseObject<>(
                tClassSubjectExtendRepository.getClassSubjectIdByRequest(tClassSubjectRequest),
                HttpStatus.OK,
                "Lấy id lớp môn thành công!"
        );
    }
}
