package fplhn.udpm.examdistribution.core.teacher.trackhistory.controller;

import fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request.ListStudentMakeMistakeRequest;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.service.TTrackHistoryService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(MappingConstants.API_TEACHER_TRACK_HISTORY)
@RequiredArgsConstructor
public class TTrackHistoryRestController {

    private final TTrackHistoryService trackHistoryService;

    @GetMapping
    public ResponseEntity<?> getListStudentMakeMistake(ListStudentMakeMistakeRequest request) {
        return Helper.createResponseEntity(trackHistoryService.getListStudentMakeMistake(request));
    }

}
