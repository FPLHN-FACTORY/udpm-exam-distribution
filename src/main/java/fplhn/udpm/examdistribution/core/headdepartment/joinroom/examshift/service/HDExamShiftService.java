package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.request.HDExamShiftRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface HDExamShiftService {

    boolean getExamShiftByRequest(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> joinExamShift(@Valid HDExamShiftRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

}
