package fplhn.udpm.examdistribution.core.teacher.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.TClassSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.classsubject.repository.TClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.classsubject.service.TClassSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassSubjectTeacherServiceImpl implements TClassSubjectService {

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
