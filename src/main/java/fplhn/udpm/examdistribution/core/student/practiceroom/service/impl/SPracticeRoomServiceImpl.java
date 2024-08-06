package fplhn.udpm.examdistribution.core.student.practiceroom.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.common.base.FileResponse;
import fplhn.udpm.examdistribution.core.student.practiceroom.model.request.SPracticeRoomRequest;
import fplhn.udpm.examdistribution.core.student.practiceroom.model.response.SPRMockExamPaperResponse;
import fplhn.udpm.examdistribution.core.student.practiceroom.model.response.SPracticeRoomResponse;
import fplhn.udpm.examdistribution.core.student.practiceroom.repository.SPRExamPaperRepository;
import fplhn.udpm.examdistribution.core.student.practiceroom.repository.SPRStudentRepository;
import fplhn.udpm.examdistribution.core.student.practiceroom.repository.SPracticeRoomRepository;
import fplhn.udpm.examdistribution.core.student.practiceroom.repository.SStudentPracticeRoomRepository;
import fplhn.udpm.examdistribution.core.student.practiceroom.service.SPracticeRoomService;
import fplhn.udpm.examdistribution.entity.PracticeRoom;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.entity.StudentPracticeRoom;
import fplhn.udpm.examdistribution.infrastructure.config.drive.service.GoogleDriveFileService;
import fplhn.udpm.examdistribution.infrastructure.config.redis.service.RedisService;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.*;

@Service
@RequiredArgsConstructor
@Validated
public class SPracticeRoomServiceImpl implements SPracticeRoomService {

    private final SPracticeRoomRepository practiceRoomRepository;

    private final SStudentPracticeRoomRepository studentPracticeRoomRepository;

    private final SPRStudentRepository studentRepository;

    private final HttpSession session;

    private final SPRExamPaperRepository examPaperRepository;

    private final GoogleDriveFileService googleDriveFileService;

    private final RedisService redisService;

    @Override
    public ResponseObject<?> join(@Valid SPracticeRoomRequest request) {
        try {
            Optional<Student> student = studentRepository.findById(request.getStudentId());
            if (student.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Sinh viên không tồn tại"
                );
            }
            List<PracticeRoom> practiceRooms = practiceRoomRepository.findAllByPracticeRoomCodeAndPasswordAndStatusAndFacility_Id(
                    request.getPracticeRoomCodeJoin(),
                    request.getPasswordJoin(),
                    EntityStatus.ACTIVE
                    , session.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString()
            );
            if (practiceRooms.isEmpty()) {
                return new ResponseObject<>(
                        null,
                        HttpStatus.BAD_REQUEST,
                        "Mã phòng hoặc mật khẩu không đúng"
                );
            }
            List<StudentPracticeRoom> studentPracticeRooms =
                    studentPracticeRoomRepository.findAllByStudent_IdAndPracticeRoom_Id(
                            request.getStudentId(),
                            practiceRooms.get(0).getId()
                    );
            if (studentPracticeRooms.isEmpty()) {
                StudentPracticeRoom studentPracticeRoom = new StudentPracticeRoom();
                studentPracticeRoom.setJoinedAt(new Date().getTime());
                studentPracticeRoom.setStudent(student.get());
                studentPracticeRoom.setPracticeRoom(practiceRooms.get(0));
                studentPracticeRoomRepository.save(studentPracticeRoom);
            } else {
                studentPracticeRooms.get(0).setJoinedAt(new Date().getTime());
                studentPracticeRoomRepository.save(studentPracticeRooms.get(0));
            }
//            if (request.getIsTakeNewMockExamPaper()) {
                SPRMockExamPaperResponse mockExamPaper = getMockExamPaper(
                        practiceRooms.get(0).getSubject().getId(),
                        practiceRooms.get(0).getBlock().getId());
                if (mockExamPaper == null) {
                    return new ResponseObject<>(
                            null, HttpStatus.BAD_REQUEST, "Môn học này chưa có đề thi thử"
                    );
                }
                Resource resource = googleDriveFileService.loadFile(mockExamPaper.getPath());
                String data = Base64.getEncoder().encodeToString(resource.getContentAsByteArray());
                String key = mockExamPaper.getId();
//                redisService.set(key, data);
                PracticeRoom res = practiceRooms.get(0);
                return new ResponseObject<>(
                        new SPracticeRoomResponse(
                                res.getId(),
                                request.getPracticeRoomCodeJoin(),
                                request.getPasswordJoin(),
                                true,
                                key,
                                new FileResponse(data, resource.getFilename())
                        ),
                        HttpStatus.OK,
                        "Tham gia thành công"
                );
//            } else {
//                Object redisValue = redisService.get(request.getKey());
//                if (redisValue != null) {
//                    return new ResponseObject<>(
//                            new SPracticeRoomResponse(
//                                    practiceRooms.get(0).getId(),
//                                    request.getPracticeRoomCodeJoin(),
//                                    request.getPasswordJoin(),
//                                    false,
//                                    request.getKey(),
//                                    new FileResponse(redisValue.toString(), "fileName")
//                            ),
//                            HttpStatus.OK,
//                            "Lấy đề thi thử thành công"
//                    );
//                }
//                return new ResponseObject<>(
//                        null,
//                        HttpStatus.BAD_REQUEST,
//                        "Đề thi thử không tồn tại"
//                );
//            }

        } catch (Exception e) {
            return new ResponseObject<>(
                    null, HttpStatus.BAD_REQUEST, "Có lỗi xảy ra khi vào phòng thi thử"
            );
        }
    }

    public SPRMockExamPaperResponse getMockExamPaper(String subjectId, String blockId) {
        String facilityId = session.getAttribute(SessionConstant.CURRENT_USER_FACILITY_ID).toString();
        List<SPRMockExamPaperResponse> mockExamPaperResponses =
                examPaperRepository.getMockExamPapers(subjectId, facilityId, blockId);
        if (mockExamPaperResponses != null && !mockExamPaperResponses.isEmpty()) {
            Random random = new Random();
            int randomIndex = random.nextInt(mockExamPaperResponses.size());
            return mockExamPaperResponses.get(randomIndex);
        }
        return null;
    }

}
