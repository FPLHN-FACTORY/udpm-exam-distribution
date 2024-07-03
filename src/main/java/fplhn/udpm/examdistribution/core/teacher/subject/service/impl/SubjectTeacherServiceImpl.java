package fplhn.udpm.examdistribution.core.teacher.subject.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.subject.repository.SubjectTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.subject.service.SubjectTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubjectTeacherServiceImpl implements SubjectTeacherService {

    private final SubjectTeacherExtendRepository subjectTeacherExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        return new ResponseObject<>(
                subjectTeacherExtendRepository.findAllByClassSubjectCode(classSubjectCode),
                HttpStatus.OK,
                "Lấy danh sách môn học thành công!"
        );
    }

}
