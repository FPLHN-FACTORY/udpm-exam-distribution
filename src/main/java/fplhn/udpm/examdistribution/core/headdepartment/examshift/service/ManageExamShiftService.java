package fplhn.udpm.examdistribution.core.headdepartment.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ModifyExamShiftRequest;

public interface ManageExamShiftService {

    ResponseObject<?> getAllExamShifts(ExamShiftRequest request);

    ResponseObject<?> getBlockInfo(String semesterId);

    ResponseObject<?> editExamShift(String examShiftId, ModifyExamShiftRequest request);

}
