package fplhn.udpm.examdistribution.core.student.trackstudent.controller;

import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.CheckRoomIsValidRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.InfoRoomRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.RemoveTabRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.SaveTrackUrlRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.service.StudentExamShiftTrackService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_STUDENT_TRACKER)
@CrossOrigin("*")
public class StudentExamShiftTrackRestController {

    private final StudentExamShiftTrackService studentExamShiftTrackService;

    @PostMapping("/check-room-is-valid")
    public ResponseEntity<?> checkExamShiftIsValid(@RequestBody CheckRoomIsValidRequest request) {
        return Helper.createResponseEntity(studentExamShiftTrackService.checkExamShiftIsValid(request));
    }

    @PostMapping("/exam-shift-info")
    public ResponseEntity<?> getInfoRoom(@RequestBody InfoRoomRequest request) {
        return Helper.createResponseEntity(studentExamShiftTrackService.getExamShiftInfo(request));
    }

    @PostMapping("/save-track-url")
    public ResponseEntity<?> saveTrackUrl(@RequestBody SaveTrackUrlRequest request) {
        return Helper.createResponseEntity(studentExamShiftTrackService.saveTrackUrl(request));
    }

    @PostMapping("/remove-tab")
    public ResponseEntity<?> removeTab(@RequestBody RemoveTabRequest request) {
        return Helper.createResponseEntity(studentExamShiftTrackService.removeTab(request));
    }

}
