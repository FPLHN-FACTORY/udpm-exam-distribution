package fplhn.udpm.examdistribution.core.headdepartment.joinroom.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDStudentService;
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
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_STUDENT)
public class HDStudentRestController {

    private final HDStudentService hdStudentService;

    @GetMapping("/{examShiftCode}")
    public ResponseEntity<?> findAllStudentByExamShiftCode(@PathVariable String examShiftCode) {
        return Helper.createResponseEntity(hdStudentService.findAllStudentByExamShiftCode(examShiftCode));
    }

}
