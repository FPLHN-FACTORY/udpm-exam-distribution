package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSFacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSFacilityChildService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HSFacilityChildServiceImpl implements HSFacilityChildService {

    private final HSFacilityChildExtendRepository hsFacilityChildExtendRepository;

    @Override
    public ResponseObject<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        try {
            return new ResponseObject<>(
                    hsFacilityChildExtendRepository.findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId),
                    HttpStatus.OK,
                    "Lấy danh sách campus thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách campus: {}", e.getMessage());
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách campus!"
            );
        }
    }
}
