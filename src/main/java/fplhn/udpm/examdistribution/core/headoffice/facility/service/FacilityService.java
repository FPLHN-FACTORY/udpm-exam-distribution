package fplhn.udpm.examdistribution.core.headoffice.facility.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.CreateUpdateSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.subject.model.request.SubjectRequest;
import jakarta.validation.Valid;

public interface FacilityService {

    ResponseObject<?> getAllFacility(FacilityRequest request);

    ResponseObject<?> createFacility(@Valid CreateUpdateFacilityRequest request);

    ResponseObject<?> updateFacility(String FacilityId, @Valid CreateUpdateFacilityRequest request);

    ResponseObject<?> changeFacilityStatus(String FacilityId);

    ResponseObject<?> getFacilityById(String FacilityId);

}
