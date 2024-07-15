package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TCreateExamShiftRequest;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TJoinExamShiftRequest;
import jakarta.validation.Valid;

import java.io.IOException;

public interface TExamShiftService {

    boolean findUsersInExamShift(String examShiftCode);

    ResponseObject<?> createExamShift(@Valid TCreateExamShiftRequest tCreateExamShiftRequest);

    ResponseObject<?> getExamShiftByCode(String examShiftCode);

    ResponseObject<?> joinExamShift(@Valid TJoinExamShiftRequest tJoinExamShiftRequest);

    ResponseObject<?> countStudentInExamShift(String examShiftCode);

    ResponseObject<?> removeStudent(String examShiftCode, String studentId, String reason);

    ResponseObject<?> approveStudent(String examShiftCode, String studentId);

    ResponseObject<?> refuseStudent(String examShiftCode, String studentId);

    ResponseObject<?> startExamShift(String examShiftCode, String passwordExamPaperShift);

    ResponseObject<?> getPathByExamShiftCode(String examShiftCode);

    ResponseObject<?> getFile(String file) throws IOException;

    ResponseObject<?> updateStatusExamShift(String examShiftCode);

}