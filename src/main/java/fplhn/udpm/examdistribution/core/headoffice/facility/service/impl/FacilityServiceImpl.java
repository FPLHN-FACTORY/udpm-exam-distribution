package fplhn.udpm.examdistribution.core.headoffice.facility.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.CreateUpdateFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.repository.FacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.facility.service.FacilityService;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.Helper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class FacilityServiceImpl implements FacilityService {

    private final FacilityExtendRepository facilityExtendRepository;

    @Override
    public ResponseObject<?> getAllFacility(FacilityRequest request) {
        Pageable pageable = Helper.createPageable(request, "createdDate");
        return new ResponseObject<>(
                PageableObject.of(facilityExtendRepository.getAllFacility(pageable, request)),
                HttpStatus.OK,
                "Get all facility successfully"
        );
    }

    @Override
    public ResponseObject<?> createFacility(CreateUpdateFacilityRequest request) {
        List<Facility> facilityOptional = facilityExtendRepository.findAllByName(request.getFacilityName().trim());
        if (!facilityOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Cơ sở đã tồn tại");
        }
        String code = Helper.generateCodeFromName(request.getFacilityName());
        Facility facility = new Facility();
        facility.setCode(code);
        facility.setName(Helper.replaceManySpaceToOneSpace(request.getFacilityName()));
        facility.setCreatedDate(System.currentTimeMillis());
        facility.setStatus(EntityStatus.ACTIVE);
        facilityExtendRepository.save(facility);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm cơ sở thành công");
    }

    @Override
    public ResponseObject<?> updateFacility(String facilityId, CreateUpdateFacilityRequest request) {
        if (facilityExtendRepository.existsByNameAndIdNot(request.getFacilityName(), facilityId)) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Cơ sở đã tồn tại");
        }

        Optional<Facility> facilityOptional = facilityExtendRepository.findById(facilityId);


        facilityOptional.map(facility -> {
            facility.setCode(Helper.generateCodeFromName(request.getFacilityName().trim()));
            facility.setName(Helper.replaceManySpaceToOneSpace(  request.getFacilityName().trim()));
            facility.setCreatedDate(facility.getCreatedDate());
            facility.setStatus(EntityStatus.ACTIVE);
            return facilityExtendRepository.save(facility);
        });

        return facilityOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật cơ sở thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở"));
    }

    @Override
    public ResponseObject<?> changeFacilityStatus(String facilityId) {
        Optional<Facility> facilityOptional = facilityExtendRepository.findById(facilityId);

        facilityOptional.map(facility -> {
            facility.setName(facility.getName());
            facility.setCreatedDate(facility.getCreatedDate());
            facility.setStatus(facility.getStatus() != EntityStatus.ACTIVE ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
            return facilityExtendRepository.save(facility);
        });

        return facilityOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Đổi trạng thái cơ sở thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Không tìm thấy cơ sở"));
    }

    @Override
    public ResponseObject<?> getFacilityById(String facilityId) {
        return facilityExtendRepository.getDetailFacilityById(facilityId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Get facility successfully"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Facility not found"));
    }

}
