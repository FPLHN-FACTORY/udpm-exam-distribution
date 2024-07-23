package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.HSExamShiftServiceRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface HSExamShiftService {

    boolean getExamShiftByRequest(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> joinExamShift(@Valid HSExamShiftServiceRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

}
