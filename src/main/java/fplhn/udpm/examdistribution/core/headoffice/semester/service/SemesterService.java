package fplhn.udpm.examdistribution.core.headoffice.semester.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.CreateUpdateSemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.SemesterRequest;
import jakarta.validation.Valid;

public interface SemesterService {

    ResponseObject<?> getAllSemester(SemesterRequest request);

    ResponseObject<?> createSemester(@Valid CreateUpdateSemesterRequest createUpdateSemesterRequest);

    ResponseObject<?> updateSemester(String semesterId, @Valid CreateUpdateSemesterRequest createUpdateSemesterRequest);

    ResponseObject<?> getSemesterById(String semesterId);

    ResponseObject<?> statusChangeSemester(String semesterId);

}
