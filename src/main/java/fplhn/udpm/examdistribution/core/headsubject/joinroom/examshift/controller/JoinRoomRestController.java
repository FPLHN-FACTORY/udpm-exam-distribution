package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.JoinRoomRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.JoinRoomService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_MANAGE_JOIN_ROOM)
public class JoinRoomRestController {

    private final JoinRoomService joinRoomService;

    @PostMapping
    public ResponseEntity<?> joinExamShift(@RequestBody JoinRoomRequest joinRoomRequest) {
        return Helper.createResponseEntity(joinRoomService.joinExamShift(joinRoomRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(joinRoomService.getExamShiftByCode(examShiftCode));
    }

    @GetMapping("/{examShiftCode}/count-student")
    public ResponseEntity<?> countStudentInExamShift(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(joinRoomService.countStudentInExamShift(examShiftCode));
    }

}
