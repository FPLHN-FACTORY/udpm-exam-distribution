package fplhn.udpm.examdistribution.core.teacher.facilitychild.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.facilitychild.repository.FacilityChildTeacherExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.facilitychild.service.FacilityChildTeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacilityChildTeacherServiceImpl implements FacilityChildTeacherService {

    private final FacilityChildTeacherExtendRepository facilityChildTeacherExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                facilityChildTeacherExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách campus thành công!"
        );
    }
}
