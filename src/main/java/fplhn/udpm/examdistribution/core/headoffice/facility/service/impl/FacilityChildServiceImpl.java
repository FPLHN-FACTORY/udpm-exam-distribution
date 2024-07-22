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

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
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
        Optional<Facility> facilityOptional = facilityRepository.findById(facilityId);
        if (facilityOptional.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Cơ sở không tồn tại");
        }

        List<FacilityChild> facilityChilds = facilityChildExtendRepository.findAllByCode(Helper.replaceSpaceToEmpty(request.getFacilityChildCode().trim()));
        if (!facilityChilds.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mã cơ sở con đã tồn tại");
        }
        FacilityChild facilityChild = new FacilityChild();
        facilityChild.setCode(Helper.replaceSpaceToEmpty(request.getFacilityChildCode().trim()));
        facilityChild.setName(Helper.replaceManySpaceToOneSpace(request.getFacilityChildName().trim()));
        facilityChild.setCreatedDate(System.currentTimeMillis());
        facilityChild.setStatus(EntityStatus.ACTIVE);
        facilityChild.setFacility(facilityOptional.get());
        facilityChildExtendRepository.save(facilityChild);

        return new ResponseObject<>(null, HttpStatus.CREATED, "Thêm cơ sở con thành công");
    }

    @Override
    public ResponseObject<?> updateFacilityChild(String facilityChildId, CreateUpdateFacilityChildRequest request) {
        Optional<FacilityChild> facilityChildOptional = facilityChildExtendRepository.findById(facilityChildId);
        List<FacilityChild> facilityChild = facilityChildExtendRepository.findAllByCode(Helper.replaceSpaceToEmpty(request.getFacilityChildCode().trim()));
        if (!facilityChild.isEmpty()) {
            for (FacilityChild facilityChildItem : facilityChild){
                if (!facilityChildItem.getId().equals(facilityChildId)){
                    return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Mã cơ sở con đã tồn tại");
                }
            }
        }
        facilityChildOptional.map(facility -> {
            facility.setCode(Helper.replaceSpaceToEmpty(request.getFacilityChildCode().trim()));
            facility.setName(Helper.replaceManySpaceToOneSpace(request.getFacilityChildName().trim()));
            facility.setCreatedDate(facility.getCreatedDate());
            facility.setStatus(facility.getStatus());
            return facilityChildExtendRepository.save(facility);
        });

        return facilityChildOptional
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật cơ sở con thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cơ sở con không tồn tại"));
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
                .map(subject -> new ResponseObject<>(null, HttpStatus.OK, "Cập nhật trạng thái cơ sở con thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cơ sở con không tồn tại"));
    }

    @Override
    public ResponseObject<?> getFacilityByIdChild(String facilityChildId) {
        return facilityChildExtendRepository.getDetailFacilityChildById(facilityChildId)
                .map(subject -> new ResponseObject<>(subject, HttpStatus.OK, "Lấy cơ sở con thành công"))
                .orElseGet(() -> new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Cơ sở con không tồn tại"));
    }


}
