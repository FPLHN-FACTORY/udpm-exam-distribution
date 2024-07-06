package fplhn.udpm.examdistribution.core.student.home.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.home.model.request.StudentExamShiftRequest;
import jakarta.validation.Valid;

public interface StudentExamShiftService {

    ResponseObject<?> joinExamShift(@Valid StudentExamShiftRequest studentExamShiftRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

}
