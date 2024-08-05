package fplhn.udpm.examdistribution.core.headsubject.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSExamShiftServiceRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface HSExamShiftService {

    boolean getExamShiftByRequest(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> createExamShift(@Valid HSCreateExamShiftRequest hsCreateExamShiftRequest);

    ResponseObject<?> joinExamShift(@Valid HSExamShiftServiceRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> getStartTimeEndTimeExamPaperByExamShiftCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

}
