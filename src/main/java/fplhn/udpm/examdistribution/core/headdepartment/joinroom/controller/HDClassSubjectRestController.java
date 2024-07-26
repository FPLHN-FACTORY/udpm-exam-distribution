package fplhn.udpm.examdistribution.core.headdepartment.joinroom.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.service.HDClassSubjectService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_DEPARTMENT_CLASS_SUBJECT)
public class HDClassSubjectRestController {

    private final HDClassSubjectService hdClassSubjectService;

    @GetMapping("/get-class-subject")
    public ResponseEntity<?> getClassSubject(String classSubjectCode) {
        return Helper.createResponseEntity(hdClassSubjectService.getClassSubject(classSubjectCode));
    }

    @GetMapping
    public ResponseEntity<?> getClassSubjectIdByRequest(HDClassSubjectRequest hdClassSubjectRequest) {
        return Helper.createResponseEntity(hdClassSubjectService
                .getClassSubjectIdByRequest(hdClassSubjectRequest));
    }

}
