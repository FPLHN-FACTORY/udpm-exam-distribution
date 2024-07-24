package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.service.HSClassSubjectService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(MappingConstants.API_HEAD_SUBJECT_CLASS_SUBJECT)
public class HSClassSubjectRestController {

    private final HSClassSubjectService hsClassSubjectService;

    @GetMapping
    public ResponseEntity<?> getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest) {
        return Helper.createResponseEntity(hsClassSubjectService
                .getClassSubjectIdByRequest(hsClassSubjectRequest));
    }

}
