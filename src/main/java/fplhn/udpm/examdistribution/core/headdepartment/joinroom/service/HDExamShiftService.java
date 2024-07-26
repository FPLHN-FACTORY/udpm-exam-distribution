package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDExamShiftJoinRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface HDExamShiftService {

    boolean getExamShiftByRequest(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> createExamShift(@Valid HDCreateExamShiftRequest hdCreateExamShiftRequest);

    ResponseObject<?> joinExamShift(@Valid HDExamShiftJoinRequest joinRoomRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

}
