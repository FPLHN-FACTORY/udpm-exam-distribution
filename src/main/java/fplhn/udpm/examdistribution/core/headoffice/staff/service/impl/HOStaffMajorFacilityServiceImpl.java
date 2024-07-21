package fplhn.udpm.examdistribution.core.headoffice.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffMajorFacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffMajorFacilityDetailResponse;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffDepartmentFacilityRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffMajorFacilityRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffMajorFacilityStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffMajorFacilityService;
import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Validated
public class HOStaffMajorFacilityServiceImpl implements HOStaffMajorFacilityService {

    private final HOStaffMajorFacilityRepository majorFacilityRepository;

    private final HOStaffDepartmentFacilityRepository departmentFacilityRepository;

    private final HOStaffMajorFacilityStaffRepository staffMajorFacilityStaffRepository;

    private final HOStaffRepository staffRepository;

    @Override
    public ResponseObject<?> getStaffMajorFacilities(String staffId) {
        return new ResponseObject<>(majorFacilityRepository.getMajorFacilities(staffId),
                HttpStatus.OK,
                "Lấy danh sách bộ môn chuyên ngành thành công");
    }

    @Override
    public ResponseObject<?> getDepartmentByFacility(String idFacility) {
        return new ResponseObject<>(majorFacilityRepository.getDepartmentByFacility(idFacility),
                HttpStatus.OK,
                "Lấy danh sách bộ môn theo cơ sở thành công");
    }

    @Override
    public ResponseObject<?> getMajorByDepartmentFacility(String idDepartment, String idFacility) {
        return new ResponseObject<>(majorFacilityRepository.getMajorByDepartmentFacility(idDepartment, idFacility),
                HttpStatus.OK,
                "Lấy chuyên ngành theo bộ môn theo cơ sở thành công");
    }

    @Override
    public ResponseObject<?> createStaffMajorFacility(@Valid HOStaffMajorFacilityRequest req) {

        List<DepartmentFacility> departmentFacility =
                departmentFacilityRepository.findAllByDepartment_IdAndFacility_IdAndStatus(req.getIdDepartment(),
                        req.getIdFacility(),
                        EntityStatus.ACTIVE);

        if (departmentFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Bộ môn theo cơ sở không tồn tại");
        }

        List<MajorFacility> majorFacility =
                majorFacilityRepository.findAllByDepartmentFacility_IdAndMajor_IdAndStatus(departmentFacility.get(0).getId(),
                        req.getIdMajor(),
                        EntityStatus.ACTIVE);

        if (majorFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Chuyên ngành theo cơ sở không tồn tại");
        }

        Optional<Staff> staff = staffRepository.findByIdAndStatus(req.getIdStaff(), EntityStatus.ACTIVE);

        if (staff.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Nhân viên không tồn tại");
        }

        List<StaffMajorFacility> staffMajorFacilities = staffMajorFacilityStaffRepository.checkStaffMajorFacilityExists(req.getIdFacility(), req.getIdStaff());

        if (!staffMajorFacilities.isEmpty() && staffMajorFacilities.get(0).getStatus() == EntityStatus.ACTIVE) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Một cơ sở chỉ được một bộ môn và chuyên ngành");
        }

        StaffMajorFacility staffMajorFacility = new StaffMajorFacility();
        staffMajorFacility.setMajorFacility(majorFacility.get(0));
        staffMajorFacility.setStaff(staff.get());
        staffMajorFacility.setStatus(EntityStatus.ACTIVE);
        staffMajorFacilityStaffRepository.save(staffMajorFacility);
        return new ResponseObject<>(null,
                HttpStatus.OK,
                "Thêm bộ môn chuyên ngành theo cơ sở thành công");
    }

    @Override
    public ResponseObject<?> updateStaffMajorFacility(@Valid HOStaffMajorFacilityRequest req) {

        Optional<StaffMajorFacility> staffMajorFacility = staffMajorFacilityStaffRepository.findById(req.getIdStaffMajorFacility());

        if (staffMajorFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "nhân viên chuyên ngành theo cơ sở không tồn tại");
        }

        List<DepartmentFacility> departmentFacility =
                departmentFacilityRepository.findAllByDepartment_IdAndFacility_IdAndStatus(req.getIdDepartment(),
                        req.getIdFacility(),
                        EntityStatus.ACTIVE);

        if (departmentFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Bộ môn theo cơ sở không tồn tại");
        }

        List<MajorFacility> majorFacility =
                majorFacilityRepository.findAllByDepartmentFacility_IdAndMajor_IdAndStatus(departmentFacility.get(0).getId(),
                        req.getIdMajor(),
                        EntityStatus.ACTIVE);

        if (majorFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Chuyên ngành theo cơ sở không tồn tại");
        }

        staffMajorFacility.get().setMajorFacility(majorFacility.get(0));
        staffMajorFacility.get().setStatus(EntityStatus.ACTIVE);
        staffMajorFacilityStaffRepository.save(staffMajorFacility.get());
        return new ResponseObject<>(null,
                HttpStatus.OK,
                "Sửa bộ môn chuyên ngành theo cơ sở thành công");
    }

    @Override
    public ResponseObject<?> deleteStaffMajorFacility(String idStaffMajorFacility) {

        Optional<StaffMajorFacility> staffMajorFacility = staffMajorFacilityStaffRepository.findById(idStaffMajorFacility);

        if (staffMajorFacility.isEmpty()) {
            return new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Chuyên ngành theo cơ sở không tồn tại");
        }

        staffMajorFacilityStaffRepository.deleteById(idStaffMajorFacility);

        return new ResponseObject<>(null,
                HttpStatus.OK,
                "Xoá thành công.");

    }

    @Override
    public ResponseObject<?> detailStaffMajorFacility(String idStaffMajorFacility) {
        List<HOStaffMajorFacilityDetailResponse> detailResponses = staffMajorFacilityStaffRepository.detailStaffMajorFacility(idStaffMajorFacility);
        if (detailResponses.isEmpty()) {
            new ResponseObject<>(null,
                    HttpStatus.BAD_REQUEST,
                    "Chuyên ngành bộ môn của nhân viên này không tồn tại");
        }
        return new ResponseObject<>(detailResponses.get(0), HttpStatus.OK, "Lấy chuyên ngành bộ môn thành công");
    }
}
