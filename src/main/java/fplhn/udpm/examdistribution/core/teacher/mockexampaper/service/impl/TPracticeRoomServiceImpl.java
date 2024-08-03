package fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TPracticeRoomRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TMEPPracticeRoomResponse;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository.*;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.TMEPPracticeRoomService;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.PracticeRoom;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TPracticeRoomServiceImpl implements TMEPPracticeRoomService {

    private final TMEPPracticeRoomRepository practiceRoomRepository;

    private final HttpSession httpSession;

    private final TMEPBlockRepository blockRepository;

    private final TMEPFacilityRepository facilityRepository;

    private final TMEPStaffRepository staffRepository;

    private final TMEPSubjectRepository subjectRepository;

    private final TMEPStudentPracticeRoomRepository studentPracticeRoomRepository;

    @Override
    public ResponseObject<?> createPracticeRoom(TPracticeRoomRequest request) {

        if (request.getEndDate() != null && new Date().getTime() > request.getEndDate()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Thời gian kết thúc không được ở quá khứ"
            );
        }

        PracticeRoom practiceRoom = new PracticeRoom();

        String currentBlockId = httpSession.getAttribute(SessionConstant.CURRENT_BLOCK_ID).toString();
        Optional<Block> currentBlock = blockRepository.findById(currentBlockId);
        practiceRoom.setBlock(currentBlock.get());

        if (request.getStartDate() == null || request.getEndDate() == null) {
            practiceRoom.setStartDate(currentBlock.get().getStartTime());
            practiceRoom.setEndDate(currentBlock.get().getEndTime());
        } else {
            practiceRoom.setStartDate(request.getStartDate());
            practiceRoom.setEndDate(request.getEndDate());
        }

        String currentStaffId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        practiceRoom.setStaff(staffRepository.findById(currentStaffId).get());

        String currentFacilityId = httpSession.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString();
        practiceRoom.setFacility(facilityRepository.findById(currentFacilityId).get());
        practiceRoom.setStatus(EntityStatus.ACTIVE);
        Optional<Subject> subject = subjectRepository.findById(request.getSubjectId());

        if (subject.isEmpty()) {
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Không tìm thấy môn học"
            );
        }
        practiceRoom.setSubject(subject.get());
        practiceRoom.setPassword(CodeGenerator.generateRandomCode().substring(0, 6).toUpperCase());
        practiceRoom.setPracticeRoomCode(subject.get().getSubjectCode() + "_" + CodeGenerator.generateRandomCode().substring(0, 3).toUpperCase());

        practiceRoomRepository.save(practiceRoom);
        return new ResponseObject<>(
                null,
                HttpStatus.OK,
                "Tạo phòng luyện tập thành công"
        );
    }

    @Override
    public ResponseObject<?> detailPracticeRoom(String practiceRoomId) {
        String staffId = httpSession.getAttribute(SessionConstant.CURRENT_USER_ID).toString();
        Optional<TMEPPracticeRoomResponse> practiceRoom = practiceRoomRepository.detail(practiceRoomId,staffId);
        if (practiceRoom.isEmpty()){
            return new ResponseObject<>(
                    null,
                    HttpStatus.BAD_REQUEST,
                    "Không tìm thấy phòng luyện tập"
            );
        }
        return new ResponseObject<>(
                practiceRoom.get(),
                HttpStatus.OK,
                "Lấy phòng luyện tập thành công"
        );
    }

    @Override
    @Scheduled(cron = "0 15 0 * * ?") //chạy lúc 0h15p
    @Transactional
    public void clearPracticeRoom() {
        List<PracticeRoom> practiceRooms = practiceRoomRepository.getAllByEndDate(new Date().getTime(),EntityStatus.ACTIVE);
        for (PracticeRoom practiceRoom : practiceRooms) {
            studentPracticeRoomRepository.deleteByPracticeRoomId(practiceRoom.getId());
            practiceRoomRepository.deleteById(practiceRoom.getId());
        }
    }

}
