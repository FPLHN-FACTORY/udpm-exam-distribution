package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.CreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.JoinExamShiftRequest;
import jakarta.validation.Valid;

public interface ExamShiftService {

    ResponseObject<?> createExamShift(@Valid CreateExamShiftRequest createExamShiftRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> joinExamShift(@Valid JoinExamShiftRequest joinExamShiftRequest);

}
