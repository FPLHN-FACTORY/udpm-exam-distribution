package fplhn.udpm.examdistribution.core.teacher.facilitychild.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.facilitychild.repository.TFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.facilitychild.service.TFacilityChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TFacilityChildServiceImpl implements TFacilityChildService {

    private final TFacilityChildExtendRepository tFacilityChildExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                tFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách campus thành công!"
        );
    }
}
