package fplhn.udpm.examdistribution.core.teacher.classsubject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.ClassSubjectTeacherRequest;
import fplhn.udpm.examdistribution.core.teacher.classsubject.repository.ClassSubjectTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.classsubject.service.ClassSubjectTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClassSubjectTeacherServiceImpl implements ClassSubjectTeacherService {

    private final ClassSubjectTeacherExtendRepository classSubjectTeacherExtendRepository;

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(ClassSubjectTeacherRequest classSubjectTeacherRequest) {
        return new ResponseObject<>(
                classSubjectTeacherExtendRepository.getClassSubjectIdByRequest(classSubjectTeacherRequest),
                HttpStatus.OK,
                "Lấy id lớp môn thành công!"
        );
    }
}
