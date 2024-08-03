package fplhn.udpm.examdistribution.core.student.practiceroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.practiceroom.model.request.SPracticeRoomRequest;
import jakarta.validation.Valid;

public interface SPracticeRoomService {

    ResponseObject<?> join(@Valid SPracticeRoomRequest sPracticeRoomRequest);

}
