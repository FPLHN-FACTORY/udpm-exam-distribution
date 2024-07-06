package fplhn.udpm.examdistribution.core.student.home.controller;

import fplhn.udpm.examdistribution.core.student.home.model.request.StudentExamShiftRequest;
import fplhn.udpm.examdistribution.core.student.home.service.StudentExamShiftService;
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
@RequestMapping(MappingConstants.API_STUDENT_EXAM_SHIFT)
@RequiredArgsConstructor
public class HomeRestController {

    private final StudentExamShiftService studentExamShiftService;

    @PostMapping("/join")
    public ResponseEntity<?> joinExamShift(@RequestBody StudentExamShiftRequest studentExamShiftRequest) {
        return Helper.createResponseEntity(studentExamShiftService.joinExamShift(studentExamShiftRequest));
    }

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> getExamShiftByCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(studentExamShiftService.getExamShiftByCode(examShiftCode));
    }

}
