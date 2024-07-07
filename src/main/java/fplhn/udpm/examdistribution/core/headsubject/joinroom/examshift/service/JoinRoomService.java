package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.JoinRoomRequest;
import jakarta.validation.Valid;

public interface JoinRoomService {

    ResponseObject<?> joinExamShift(@Valid JoinRoomRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

}
