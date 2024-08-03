package fplhn.udpm.examdistribution.core.headsubject.joinroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSubjectResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.repository.HSSubjectExtendRepository;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSSubjectService;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HSSubjectServiceImpl implements HSSubjectService {

    private final HSSubjectExtendRepository hsSubjectExtendRepository;

    private final SessionHelper sessionHelper;

    @Override
    public ResponseObject<?> findAllByClassSubjectCode(String classSubjectCode) {
        try {
            List<HSSubjectResponse> listSubject = hsSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode,
                    sessionHelper.getCurrentBlockId());
            for (HSSubjectResponse hsSubjectResponse : listSubject) {
                System.out.println(hsSubjectResponse.getId());
            }
            return new ResponseObject<>(
                    hsSubjectExtendRepository.findAllByClassSubjectCode(classSubjectCode,
                            sessionHelper.getCurrentBlockId()),
                    HttpStatus.OK,
                    "Lấy danh sách môn học thành công!"
            );
        } catch (Exception e) {
            log.error("Lỗi khi lấy danh sách môn học: ", e);
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Lỗi khi lấy danh sách môn học!"
            );
        }
    }
}
