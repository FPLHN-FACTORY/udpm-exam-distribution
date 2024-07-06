package fplhn.udpm.examdistribution.core.student.student.controller;

import fplhn.udpm.examdistribution.core.student.student.service.StudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_STUDENT_JOIN_EXAM_SHIFT)
public class StudentRestController {

    private final StudentService studentService;

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> findAllStudentByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(studentService.findAllStudentByExamShiftCode(examShiftCode));
    }

}
