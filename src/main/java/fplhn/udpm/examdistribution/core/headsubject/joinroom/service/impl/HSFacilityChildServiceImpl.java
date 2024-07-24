package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSFacilityChildService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSFacilityChildServiceImpl implements HSFacilityChildService {

    private final HSFacilityChildExtendRepository hsFacilityChildExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return new ResponseObject<>(
                hsFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                HttpStatus.OK,
                "Lấy danh sách campus thành công!"
        );
    }
}
