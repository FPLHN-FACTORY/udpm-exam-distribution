package fplhn.udpm.examdistribution.core.student.practiceroom.controller;

import fplhn.udpm.examdistribution.core.student.practiceroom.model.request.SPracticeRoomRequest;
import fplhn.udpm.examdistribution.core.student.practiceroom.service.SPracticeRoomService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_STUDENT_PRACTICE_ROOM)
@RequiredArgsConstructor
public class SPracticeRoomRestController {

    private final SPracticeRoomService practiceRoomService;

    @PostMapping
    public ResponseEntity<?> join(@RequestBody SPracticeRoomRequest request) {
        return Helper.createResponseEntity(practiceRoomService.join(request));
    }


}
