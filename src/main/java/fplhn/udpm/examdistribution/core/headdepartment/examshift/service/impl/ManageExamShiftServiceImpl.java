package fplhn.udpm.examdistribution.core.headdepartment.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ModifyExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.repository.HDBlockRepository;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.repository.HDExamShiftRepository;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.repository.HDStaffExamShiftRepository;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.service.ManageExamShiftService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.utils.DateTimeUtil;
import fplhn.udpm.examdistribution.utils.Helper;
import fplhn.udpm.examdistribution.utils.SessionHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
@Slf4j
public class ManageExamShiftServiceImpl implements ManageExamShiftService {

    private final HDExamShiftRepository hdExamShiftRepository;

    private final SessionHelper sessionHelper;

    private final HDBlockRepository hdBlockRepository;

    private final HDStaffExamShiftRepository hdStaffExamShiftRepository;

    @Override
    public ResponseObject<?> getAllExamShifts(ExamShiftRequest request) {
        if (request.getBlockId() == null) {
            request.setBlockId(sessionHelper.getCurrentBlockId());
        }
        Optional<Block> block = hdBlockRepository.findById(request.getBlockId());
        if (block.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy block phù hợp",
                    HttpStatus.NOT_FOUND
            );
        }
        Long startRangeTime = block.get().getStartTime();
        Long endRangeTime = block.get().getEndTime();
        log.info("startRangeTime: {}", startRangeTime);
        log.info("endRangeTime: {}", endRangeTime);
        return new ResponseObject<>(
                PageableObject.of(
                        hdExamShiftRepository.getAllExamShifts(
                                Helper.createPageable(request, "examDate"),
                                request,
                                startRangeTime,
                                endRangeTime
                        )
                ),
                HttpStatus.OK,
                "Lấy danh sách ca thi thành công"
        );
    }

    @Override
    public ResponseObject<?> getBlockInfo(String semesterId) {
        return new ResponseObject<>(
                hdBlockRepository.getListBlocks(semesterId == null ? sessionHelper.getCurrentSemesterId() : semesterId),
                HttpStatus.OK,
                "Lấy danh sách block thành công"
        );
    }

    @Override
    public ResponseObject<?> editExamShift(String examShiftId, ModifyExamShiftRequest request) {
        Optional<ExamShift> examShift = hdExamShiftRepository.findById(examShiftId);
        if (examShift.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy ca thi phù hợp",
                    HttpStatus.NOT_FOUND
            );
        }
        ExamShift examShiftEntity = examShift.get();
        Long currentTime = DateTimeUtil.getCurrentTime();
        if (examShiftEntity.getExamDate() < currentTime) {
            return ResponseObject.errorForward(
                    "Không thể chỉnh sửa ca thi đã diễn ra",
                    HttpStatus.BAD_REQUEST
            );
        }
        examShiftEntity.setExamDate(request.getExamDate());

        Optional<ExamShift> examShiftDuplicate = hdExamShiftRepository.findByExamDateAndRoomAndShift(
                request.getExamDate(),
                request.getRoom(),
                examShiftEntity.getShift()
        );
        if (examShiftDuplicate.isPresent() && !examShiftDuplicate.get().getId().equals(examShiftId)) {
            return ResponseObject.errorForward(
                    "Đã có ca thi sử dụng phòng - " + request.getRoom() + " vào thời gian này",
                    HttpStatus.BAD_REQUEST
            );
        }
        examShiftEntity.setRoom(request.getRoom());
        examShiftEntity.setShift(Shift.valueOf(request.getShift()));

        Optional<Staff> firstSupervisor = hdStaffExamShiftRepository.findByStaffCode(request.getFirstSupervisorCode());
        if (firstSupervisor.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy giáo viên coi thi 1 phù hợp",
                    HttpStatus.NOT_FOUND
            );
        }
        examShiftEntity.setFirstSupervisor(firstSupervisor.get());

        Optional<Staff> secondSupervisor = hdStaffExamShiftRepository.findByStaffCode(request.getSecondSupervisorCode());
        if (secondSupervisor.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy giáo viên coi thi 2 phù hợp",
                    HttpStatus.NOT_FOUND
            );
        }
        examShiftEntity.setSecondSupervisor(secondSupervisor.get());

        hdExamShiftRepository.save(examShiftEntity);
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Chỉnh sửa ca thi thành công"
        );
    }

}
