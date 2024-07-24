package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSClassSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSClassSubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HSClassSubjectServiceImpl implements HSClassSubjectService {

    private final HSClassSubjectExtendRepository hsClassSubjectExtendRepository;

    @Override
    public ResponseObject<?> getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest) {
        return new ResponseObject<>(
                hsClassSubjectExtendRepository.getClassSubjectIdByRequest(hsClassSubjectRequest),
                HttpStatus.OK,
                "Lấy id lớp môn thành công!"
        );
    }

}
