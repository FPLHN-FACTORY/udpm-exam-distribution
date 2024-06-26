package fplhn.udpm.examdistribution.core.headoffice.department.department.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.CreateOrUpdateMajorRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.department.model.request.FindMajorRequest;
import jakarta.validation.Valid;

public interface MajorService {

    ResponseObject<?> getAllMajor(String id, FindMajorRequest request);

    ResponseObject<?> addMajor(@Valid CreateOrUpdateMajorRequest request);

    ResponseObject<?> updateMajor(@Valid CreateOrUpdateMajorRequest request, String id);

    ResponseObject<?> deleteMajor(String id);

}
