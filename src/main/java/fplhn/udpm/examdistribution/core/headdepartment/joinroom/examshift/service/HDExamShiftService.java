package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import jakarta.validation.Valid;

public interface HDExamShiftService {

    ResponseObject<?> joinExamShift(@Valid HDExamShiftRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

}
