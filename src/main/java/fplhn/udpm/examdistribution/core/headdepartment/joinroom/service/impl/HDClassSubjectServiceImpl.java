package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository.HDClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDClassSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HDClassSubjectServiceImpl implements HDClassSubjectService {

    private final HDClassSubjectExtendRepository hdClassSubjectExtendRepository;

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(HDClassSubjectRequest hdClassSubjectRequest) {
        return new ResponseObject<>(
                hdClassSubjectExtendRepository.getClassSubjectIdByRequest(hdClassSubjectRequest),
                HttpStatus.OK,
                "Lấy id lớp môn thành công!"
        );
    }
}
