package fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.impl;

import fplhn.udpm.examdistribution.core.common.base.PageableObject;
import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.CreateMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.MajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.request.UpdateMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response.FacilityDepartmentInfoResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response.MajorFacilitiesResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.model.response.ModifyMajorFacilityResponse;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFDepartmentFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFMajorExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.DFStaffExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.repository.MajorFacilityExtendRepository;
import fplhn.udpm.examdistribution.core.headoffice.department.departmentfacility.service.MajorFacilityService;
import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.entity.Major;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.utils.Helper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
public class MajorFacilityServiceImpl implements MajorFacilityService {

    private final MajorFacilityExtendRepository majorFacilityExtendRepository;

    private final DFDepartmentFacilityExtendRepository departmentFacilityRepository;

    private final DFStaffExtendRepository staffRepository;

    private final DFMajorExtendRepository majorRepository;

    @Override

    public ResponseObject<?> getAllMajorFacilities(@Valid MajorFacilityRequest request) {
        Pageable pageable = Helper.createPageable(request, "id");

        PageableObject<?> listMajorFacility = PageableObject.of(majorFacilityExtendRepository.findAllMajorFacilities(request, pageable));
        FacilityDepartmentInfoResponse facilityDepartmentInfo = majorFacilityExtendRepository.getFacilityDepartmentInfo(request.getDepartmentFacilityId());
        MajorFacilitiesResponse majorFacilitiesResponse = new MajorFacilitiesResponse();
        majorFacilitiesResponse.setMajorFacilities(listMajorFacility);
        majorFacilitiesResponse.setFacilityDepartmentInfo(facilityDepartmentInfo);
        return ResponseObject.successForward(
                majorFacilitiesResponse,
                "Lấy danh sách chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> getMajorFacilityById(String majorFacilityId) {
        return ResponseObject.successForward(
                majorFacilityExtendRepository.findMajorFacilityById(majorFacilityId),
                "Lấy thông tin chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> createMajorFacility(@Valid CreateMajorFacilityRequest request) {
        Optional<DepartmentFacility> departmentFacilityOptional = departmentFacilityRepository
                .findById(request.getDepartmentFacilityId());
        if (departmentFacilityOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy bộ môn theo cơ sở",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Staff> staffOptional = staffRepository.findById(request.getHeadMajorId());
        if (staffOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy nhân viên",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Major> majorOptional = majorRepository.findById(request.getMajorId());
        if (majorOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<MajorFacility> majorFacilityOptional = majorFacilityExtendRepository
                .findByMajor_IdAndDepartmentFacility_Id(
                        request.getMajorId(),
                        request.getDepartmentFacilityId()
                );
        if (majorFacilityOptional.isPresent()) {
            return ResponseObject.errorForward(
                    "Chuyên ngành theo cơ sở đã tồn tại",
                    HttpStatus.BAD_REQUEST
            );
        }

        MajorFacility majorFacility = new MajorFacility();
        majorFacility.setMajor(majorOptional.get());
        majorFacility.setDepartmentFacility(departmentFacilityOptional.get());
        majorFacility.setStaff(staffOptional.get());
        majorFacilityExtendRepository.save(majorFacility);

        return ResponseObject.successForward(
                new ModifyMajorFacilityResponse(
                        majorFacility.getMajor().getName(),
                        majorFacility.getDepartmentFacility().getFacility().getName(),
                        majorFacility.getDepartmentFacility().getDepartment().getName()
                ),
                "Tạo chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> updateMajorFacility(String majorFacilityId, @Valid UpdateMajorFacilityRequest request) {
        Optional<MajorFacility> majorFacilityOptional = majorFacilityExtendRepository.findById(majorFacilityId);
        if (majorFacilityOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành theo cơ sở",
                    HttpStatus.NOT_FOUND
            );
        }

        Optional<Staff> staffOptional = staffRepository.findById(request.getHeadMajorId());
        if (staffOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy nhân viên",
                    HttpStatus.BAD_REQUEST
            );
        }

        Optional<Major> majorOptional = majorRepository.findById(request.getMajorId());
        if (majorOptional.isEmpty()) {
            return ResponseObject.errorForward(
                    "Không tìm thấy chuyên ngành",
                    HttpStatus.BAD_REQUEST
            );
        }

        MajorFacility majorFacility = majorFacilityOptional.get();
        majorFacility.setMajor(majorOptional.get());
        majorFacility.setStaff(staffOptional.get());
        majorFacilityExtendRepository.save(majorFacility);

        return ResponseObject.successForward(
                new ModifyMajorFacilityResponse(
                        majorFacility.getMajor().getName(),
                        majorFacility.getDepartmentFacility().getFacility().getName(),
                        majorFacility.getDepartmentFacility().getDepartment().getName()
                ),
                "Cập nhật chuyên ngành theo cơ sở thành công"
        );
    }

    @Override
    public ResponseObject<?> getAllMajors(String departmentId) {
        return ResponseObject.successForward(
                majorRepository.findAllByDepartmentId(departmentId),
                "Lấy danh sách chuyên ngành thành công"
        );
    }

}
