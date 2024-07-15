package fplhn.udpm.examdistribution.core.teacher.exampapershift.controller;

import fplhn.udpm.examdistribution.core.teacher.exampapershift.service.TExamPaperShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_TEACHER_EXAM_PAPER_SHIFT)
public class TExamPaperShiftRestController {

    private final TExamPaperShiftService tExamPaperShiftService;

    @PutMapping("/{examShiftCode}")
    public ResponseEntity<?> updateExamShiftStatus(@PathVariable String examShiftCode) {
        return ResponseEntity.ok(tExamPaperShiftService.updateExamShiftStatus(examShiftCode));
    }

}
