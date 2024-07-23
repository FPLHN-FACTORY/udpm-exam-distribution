package fplhn.udpm.examdistribution.core.student.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.exampaper.model.request.SOpenExamPaperRequest;
import fplhn.udpm.examdistribution.core.student.examshift.model.request.SExamShiftRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface SExamShiftService {

    boolean findStudentInExamShift(String examShiftCode);

    ResponseObject<?> joinExamShift(@Valid SExamShiftRequest sExamShiftRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getExamShiftPaperByExamShiftCode(String file) throws IOException;

    ResponseObject<?> openExamPaper(SOpenExamPaperRequest sOpenExamPaperRequest);

    ResponseObject<?> updateExamStudentStatus(String examShiftCode);

}
