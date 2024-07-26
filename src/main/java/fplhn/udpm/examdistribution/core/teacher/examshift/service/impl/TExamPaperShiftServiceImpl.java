package fplhn.udpm.examdistribution.core.teacher.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.repository.TExamPaperShiftExtendRepository;
import fplhn.udpm.examdistribution.core.teacher.examshift.service.TExamPaperShiftService;
import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TExamPaperShiftServiceImpl implements TExamPaperShiftService {

    private final TExamPaperShiftExtendRepository tExamPaperShiftExtendRepository;

    @Override
    public ResponseObject<?> updateExamShiftStatus(String examShiftCode) {
        try {
            String examPaperShiftId = tExamPaperShiftExtendRepository.findExamPaperShiftIdByExamShiftCode(examShiftCode);
            if (examPaperShiftId == null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy id!");
            }

            ExamPaperShift examPaperShift = tExamPaperShiftExtendRepository.getReferenceById(examPaperShiftId);
            examPaperShift.setExamShiftStatus(ExamShiftStatus.FINISHED);
            tExamPaperShiftExtendRepository.save(examPaperShift);

            return new ResponseObject<>(null, HttpStatus.OK, "Cập nhật trạng thái ca thi thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật trạng thái ca thi: {}", e.getMessage());
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Lỗi khi cập nhật trạng thái ca thi!");
        }
    }

}
