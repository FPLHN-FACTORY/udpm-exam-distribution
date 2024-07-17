package fplhn.udpm.examdistribution.core.teacher.trackhistory.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request.ListViolationStudentRequest;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.repository.TTrackHistoryRepository;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.service.TTrackHistoryService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TTrackHistoryServiceImpl implements TTrackHistoryService {

    private final TTrackHistoryRepository trackHistoryRepository;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getListViolationStudent(ListViolationStudentRequest request) {
        try {
            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            Pageable pageable = Helper.createPageable(request, "createdDate");
            return new ResponseObject<>(
                    PageableObject.of(trackHistoryRepository.getListViolationStudent(pageable, request.getExamShiftCode(), request.getStudentId(), blockId)),
                    HttpStatus.OK,
                    "Lấy thành công danh sách sinh viên phạm lỗi"
            );
        } catch (Exception e) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không lấy được danh sách học sinh phạm lỗi");
        }
    }

}
