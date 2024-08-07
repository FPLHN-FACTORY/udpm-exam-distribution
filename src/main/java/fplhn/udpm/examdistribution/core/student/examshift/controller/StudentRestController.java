package fplhn.udpm.examdistribution.core.student.examshift.controller;

import fplhn.udpm.examdistribution.core.student.examshift.service.SStudentService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_STUDENT_JOIN_EXAM_SHIFT)
public class StudentRestController {

    private final SStudentService sStudentService;

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> findAllStudentByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(sStudentService.findAllStudentByExamShiftCode(examShiftCode));
    }

    @GetMapping("/rejoin/{examShiftCode}")
    public ResponseEntity<?> findAllStudentRejoinByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(sStudentService.findAllStudentRejoinByExamShiftCode(examShiftCode));
    }

    @PostMapping("/kick-uninstall-ext/{examShiftCode}")
    public ResponseEntity<?> kickStudentUnInstallExt(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(sStudentService.kickStudentUnInstallExt(examShiftCode));
    }

}
