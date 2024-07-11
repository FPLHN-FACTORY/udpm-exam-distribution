package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftAndUserInfoRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.repository.HDExamShiftExtendRepository;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.HDExamShiftService;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class HDExamShiftServiceImpl implements HDExamShiftService {

    private final HDExamShiftExtendRepository hdExamShiftExtendRepository;

    private final SimpMessagingTemplate simpMessagingTemplate;

    private final HttpSession httpSession;

    @Override
    public boolean getExamShiftByRequest(String examShiftCode) {
        HDExamShiftAndUserInfoRequest hdExamShiftAndUserInfoRequest = new HDExamShiftAndUserInfoRequest();
        hdExamShiftAndUserInfoRequest.setStaffId(httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString());
        hdExamShiftAndUserInfoRequest.setDepartmentId(httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_ID).toString());
        hdExamShiftAndUserInfoRequest.setFacilityId(httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString());

        return hdExamShiftExtendRepository.findByExamShiftCode(examShiftCode).isPresent() &&
               hdExamShiftExtendRepository.getExamShiftByRequest(examShiftCode, hdExamShiftAndUserInfoRequest).isPresent();
    }

    @Override
    public ResponseObject<?> getAllExamShift() {
        HDExamShiftAndUserInfoRequest hdExamShiftAndUserInfoRequest = new HDExamShiftAndUserInfoRequest();
        hdExamShiftAndUserInfoRequest.setStaffId(httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString());
        hdExamShiftAndUserInfoRequest.setDepartmentId(httpSession.getAttribute(SessionConstant.CURRENT_USER_DEPARTMENT_ID).toString());
        hdExamShiftAndUserInfoRequest.setFacilityId(httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString());
        return new ResponseObject<>(hdExamShiftExtendRepository
                .getAllExamShift(hdExamShiftAndUserInfoRequest),
                HttpStatus.OK, "Lấy danh sách ca thi thành công!");
    }

    @Override
    public ResponseObject<?> joinExamShift(HDExamShiftRequest joinRoomRequest) {
        Optional<ExamShift> existingExamShift = hdExamShiftExtendRepository
                .findByExamShiftCode(joinRoomRequest.getExamShiftCodeJoin());
        if (existingExamShift.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.CONFLICT,
                    "Phòng thi không tồn tại!");
        }

        ExamShift examShift = existingExamShift.get();

        simpMessagingTemplate.convertAndSend("/topic/head-department-exam-shift-join",
                "Chủ nhiệm bộ môn đã tham gia phòng thi " + examShift.getExamShiftCode());

        return new ResponseObject<>(examShift.getExamShiftCode(),
                HttpStatus.OK, "Tham gia phòng thi thành công!");
    }

    public ResponseObject<?> getExamShiftByCode(String examShiftCode) {
        return new ResponseObject<>(hdExamShiftExtendRepository.getExamShiftByCode(examShiftCode),
                HttpStatus.OK, "Lấy thông tin ca thi thành công!");
    }

    @Override
    public ResponseObject<?> countStudentInExamShift(String examShiftCode) {
        return new ResponseObject<>(hdExamShiftExtendRepository.countStudentInExamShift(examShiftCode),
                HttpStatus.OK, "Đếm số sinh viên trong phòng thi thành công!");
    }

}
