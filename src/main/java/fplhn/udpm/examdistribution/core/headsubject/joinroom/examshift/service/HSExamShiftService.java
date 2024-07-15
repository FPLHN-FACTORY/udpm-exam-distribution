package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.HSExamShiftServiceRequest;
import jakarta.validation.Valid;

public interface HSExamShiftService {

    boolean getExamShiftByRequest(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> joinExamShift(@Valid HSExamShiftServiceRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

}
