package fplhn.udpm.examdistribution.core.student.home.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.home.model.request.StudentExamShiftRequest;
import fplhn.udpm.examdistribution.entity.StudentExamShift;
import jakarta.validation.Valid;

import java.io.IOException;
import java.util.Optional;

public interface StudentExamShiftService {

    boolean findStudentInExamShift(String examShiftCode);

    ResponseObject<?> joinExamShift(@Valid StudentExamShiftRequest studentExamShiftRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> getPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

}
