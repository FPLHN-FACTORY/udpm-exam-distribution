package fplhn.udpm.examdistribution.core.teacher.mockexampaper.controller;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TPracticeRoomRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.service.TMEPPracticeRoomService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(MappingConstants.API_TEACHER_PRACTICE_ROOM)
@RequiredArgsConstructor
public class TPracticeRoomRestController {

    private final TMEPPracticeRoomService practiceRoomService;

    @PostMapping
    public ResponseEntity<?> createPracticeRoom(@RequestBody TPracticeRoomRequest practiceRoom) {
        return Helper.createResponseEntity(practiceRoomService.createPracticeRoom(practiceRoom));
    }

    @GetMapping("/{practiceRoomId}")
    public ResponseEntity<?> getPracticeRoom(@PathVariable("practiceRoomId") String practiceRoomId) {
        return Helper.createResponseEntity(practiceRoomService.detailPracticeRoom(practiceRoomId));
    }

}
