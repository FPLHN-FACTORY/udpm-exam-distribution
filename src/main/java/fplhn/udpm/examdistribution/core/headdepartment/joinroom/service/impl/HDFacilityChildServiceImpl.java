package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDFacilityChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HDFacilityChildServiceImpl implements HDFacilityChildService {

    private final HDFacilityChildExtendRepository hdFacilityChildExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        try {
            return new ResponseObject<>(
                    hdFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                    HttpStatus.OK,
                    "Lấy danh sách campus thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách campus: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách campus!"
            );
        }
    }
}
