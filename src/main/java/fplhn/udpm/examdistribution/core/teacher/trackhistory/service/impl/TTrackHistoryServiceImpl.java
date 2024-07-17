package fplhn.udpm.examdistribution.core.teacher.trackhistory.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request.ListStudentMakeMistakeRequest;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.repository.TTrackHistoryRepository;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.service.TTrackHistoryService;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TTrackHistoryServiceImpl implements TTrackHistoryService {

    private final TTrackHistoryRepository trackHistoryRepository;

    private final HttpSession httpSession;

    @Override
    public ResponseObject<?> getListStudentMakeMistake(ListStudentMakeMistakeRequest request) {
        try {
            String blockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
            return new ResponseObject<>(
                    trackHistoryRepository.getListStudentMakeMistake(request.getExamShiftCode(), request.getStudentId(), blockId),
                    HttpStatus.OK,
                    "Lấy thành công danh sách sinh viên phạm lỗi"
            );
        } catch (Exception e) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Không lấy được danh sách học sinh phạm lỗi");
        }
    }

}
