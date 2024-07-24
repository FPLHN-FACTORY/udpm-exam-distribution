package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDFacilityChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDFacilityChildServiceImpl implements HDFacilityChildService {

    private final HDFacilityChildExtendRepository hdFacilityChildExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                hdFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách campus thành công!"
        );
    }
}
