package fplhn.udpm.examdistribution.core.headoffice.facility.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityChildRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.repository.FacilityChildExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.facility.repository.FacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.facility.service.FacilityChildService;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
@Validated
public class FacilityChildServiceImpl implements FacilityChildService {

    FacilityChildExtendRepository facilityChildExtendRepository;
    FacilityRepository facilityRepository;
    FacilityExtendRepository facilityExtendRepository;

    @Override
    public ResponseObject<?> getAllFacilityChild(String facilityId, FacilityRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(facilityChildExtendRepository.getAllFacilityChild(facilityId, pageable)),
                HttpStatus.OK,
                "Get all facility child successfully"
        );
    }

    @Override
    public ResponseObject<?> createFacilityChild(String facilityId, CreateUpdateFacilityChildRequest request) {
        Optional<FacilityChild> facilityChildOptional = facilityChildExtendRepository.findByName(request.getFacilityChildName().trim());
        if (facilityChildOptional.isPresent()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Facility already exists");
        }

        Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
        if (facilityOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Facility not exists");
        }

        FacilityChild facilityChild = new FacilityChild();
        facilityChild.setName(request.getFacilityChildName());
        facilityChild.setCreatedDate(System.currentTimeMillis());
        facilityChild.setStatus(EntityStatus.ACTIVE);
        facilityChild.setFacility(facilityOptional.get());
        facilityChildExtendRepository.save(facilityChild);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Create facility successfully");
    }

    @Override
    public ResponseObject<?> updateFacilityChild(String facilityChildId, CreateUpdateFacilityChildRequest request) {
        Optional<FacilityChild> facilityChildOptional = facilityChildExtendRepository.findById(facilityChildId);

        facilityChildOptional.map(facility -> {
            facility.setName(request.getFacilityChildName());
            facility.setCreatedDate(facility.getCreatedDate());
            facility.setStatus(facility.getStatus());
            return facilityChildExtendRepository.save(facility);
        });

        return facilityChildOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Update Facility child successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found"));
    }

    @Override
    public ResponseObject<?> changeFacilityStatusChild(String facilityChildId) {
        Optional<FacilityChild> facilityChildOptional = facilityChildExtendRepository.findById(facilityChildId);

        facilityChildOptional.map(facility -> {
            facility.setName(facility.getName());
            facility.setCreatedDate(facility.getCreatedDate());
            facility.setStatus(facility.getStatus() != EntityStatus.ACTIVE ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
            return facilityChildExtendRepository.save(facility);
        });

        return facilityChildOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Update status Facility child successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility child not found"));
    }

    @Override
    public ResponseObject<?> getFacilityByIdChild(String facilityChildId) {
        return facilityChildExtendRepository.getDetailFacilityChildById(facilityChildId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Get facility successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility not found"));
    }


}
