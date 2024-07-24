package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSBlockService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_BLOCK)
public class HSBlockRestController {

    private final HSBlockService hsBlockService;

    @GetMapping
    public ResponseEntity<?> findAllByClassSubjectCodeAndSubjectId(String classSubjectCode, String subjectId) {
        return Helper.createResponseEntity(hsBlockService
                .findAllByClassSubjectCodeAndSubjectId(classSubjectCode, subjectId));
    }

    @GetMapping("/block-id")
    public ResponseEntity<?> findBlockId(String classSubjectCode, String subjectId, Long examShiftDate) {
        return Helper.createResponseEntity(hsBlockService
                .findBlockId(classSubjectCode, subjectId, examShiftDate));
    }

}
