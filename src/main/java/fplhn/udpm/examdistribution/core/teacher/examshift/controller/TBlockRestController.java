package fplhn.udpm.examdistribution.core.teacher.examshift.controller;

import fplhn.udpm.examdistribution.core.teacher.examshift.service.TBlockService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_BLOCK)
public class TBlockRestController {

    private final TBlockService blockTeacherService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return Helper.createResponseEntity(blockTeacherService
                .findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId));
    }

    @GetMapping("/block-id")
    public ResponseEntity<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return Helper.createResponseEntity(blockTeacherService
                .findBlockId(classSubjectCode, subjectId, examShiftDate));
    }

}