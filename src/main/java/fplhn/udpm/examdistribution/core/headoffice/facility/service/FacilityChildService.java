package fplhn.udpm.examdistribution.core.headoffice.facility.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import jakarta.validation.Valid;

public interface FacilityChildService {

    ResponseObject<?> getAllFacilityChild(String facilityId, FacilityRequest request);

    ResponseObject<?> createFacilityChild(String facilityId,@Valid CreateUpdateFacilityChildRequest request);

    ResponseObject<?> updateFacilityChild(String facilityChildId, @Valid CreateUpdateFacilityChildRequest request);

    ResponseObject<?> changeFacilityStatusChild(String facilityChildId);

    ResponseObject<?> getFacilityByIdChild(String facilityChildId);

}
