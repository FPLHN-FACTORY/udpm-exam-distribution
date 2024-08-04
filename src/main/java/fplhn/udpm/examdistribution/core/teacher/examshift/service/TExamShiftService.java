package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TJoinExamShiftRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface TExamShiftService {

    boolean findUsersInExamShift(String examShiftCode);

    ResponseObject<?> getAllExamShift();

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> joinExamShift(@Valid TJoinExamShiftRequest tJoinExamShiftRequest);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> removeStudent(String examShiftCode, String studentId, String reason);

    ResponseObject<?> approveStudent(String examShiftCode, String studentId);

    ResponseObject<?> refuseStudent(String examShiftCode, String studentId);

    ResponseObject<?> startExamShift(String examShiftCode);

    ResponseObject<?> getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getFile(String file) throws IOException;

    ResponseObject<?> getFileExamRule(String file) throws IOException;

    ResponseObject<?> updateStatusExamShift(String examShiftCode);

}
