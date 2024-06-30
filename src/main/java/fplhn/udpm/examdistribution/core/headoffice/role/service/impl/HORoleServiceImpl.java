package fplhn.udpm.examdistribution.core.headoffice.role.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HORoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.role.model.request.HOSaveRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.role.repository.HORoleFacilityRepository;
import fplhn.udpm.examdistribution.core.headoffice.role.repository.HORoleRepository;
import fplhn.udpm.examdistribution.core.headoffice.role.repository.HORoleStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.role.service.HORoleService;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRoleRepository;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HORoleServiceImpl implements HORoleService {

    private final HORoleRepository roleRepository;

    private final HORoleFacilityRepository facilityRepository;

    private final HORoleStaffRepository staffRoleRepository;

    public HORoleServiceImpl(HORoleRepository roleRepository, HORoleFacilityRepository facilityRepository, HORoleStaffRepository staffRoleRepository) {
        this.roleRepository = roleRepository;
        this.facilityRepository = facilityRepository;
        this.staffRoleRepository = staffRoleRepository;
    }

    @Override
    public ResponseObject<?> getAllRole(HORoleRequest roleRequest) {
        Pageable page = Helper.createPageable(roleRequest, "createdDate");
        return new ResponseObject<>(roleRepository.getAllRole(page, roleRequest),
                HttpStatus.OK,
                "Get all role successfully");
    }

    @Override
    public ResponseObject<?> getOneRole(String id) {
        return new ResponseObject<>(roleRepository.findById(id),
                HttpStatus.OK,
                "Get one role successfully");
    }

    @Override
    public ResponseObject<?> deleteRole(String id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            //change status in staff_role
            List<StaffRole> staffRoles = staffRoleRepository.findAllByRole_IdAndStatus(id,EntityStatus.ACTIVE);
            staffRoles.stream().forEach(staffRole -> staffRole.setStatus(EntityStatus.INACTIVE));
            staffRoleRepository.saveAll(staffRoles);
            //change status in role
            role.get().setStatus(EntityStatus.INACTIVE);
            roleRepository.save(role.get());
            return new ResponseObject<>(null,
                    HttpStatus.OK,
                    "Delete one role successfully");
        }
        return new ResponseObject<>(null,
                HttpStatus.BAD_REQUEST,
                "Role does not exist");
    }

    @Override
    public ResponseObject<?> getFacilities() {
        return new ResponseObject<>(facilityRepository.getFacilities(), HttpStatus.OK, "Get facilities successfully");
    }

    @Override
    public ResponseObject<?> saveRole(HOSaveRoleRequest roleRequest) {
        Optional<Facility> facility = facilityRepository.findById(roleRequest.getIdFacility());
        if (facility.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.BAD_REQUEST, "Facility not found");
        }
        //create role
        if (roleRequest.getRoleId() == null || roleRequest.getRoleId().isEmpty()) {
            Role role = new Role();
            role.setId(CodeGenerator.generateRandomCode());
            role.setName(roleRequest.getRoleName());
            role.setFacility(facility.isPresent() ? facility.get() : null);
            role.setStatus(EntityStatus.ACTIVE);
            if (roleRepository.findAllByNameAndFacility_Id(roleRequest.getRoleName(), roleRequest.getIdFacility()).isEmpty()) {
                roleRepository.save(role);
                return new ResponseObject<>(null, HttpStatus.CREATED, "Role added successfully");
            } else {
                return new ResponseObject<>(null, HttpStatus.CONFLICT, "Role already exists");
            }
        }
        //update role
        else {
            Role role = roleRepository.findById(roleRequest.getRoleId()).orElse(null);
            if (role == null) {
                return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Role not found");
            } else {
                role.setName(roleRequest.getRoleName().trim());
                role.setFacility(facility.isPresent() ? facility.get() : null);
                List<Role> optionalRole = roleRepository.findAllByNameAndFacility_Id(roleRequest.getRoleName(), roleRequest.getIdFacility());
                if (optionalRole.isEmpty()) {
                    roleRepository.save(role);
                    return new ResponseObject<>(null, HttpStatus.OK, "Role updated successfully");
                } else {
                    for (Role role1 : optionalRole) {
                        if (role1.getId().equals(role.getId())) {
                            roleRepository.save(role);
                            return new ResponseObject<>(null, HttpStatus.OK, "Role updated successfully");
                        }
                    }
                    return new ResponseObject<>(null, HttpStatus.CONFLICT, "Role already exists");
                }
            }
        }
    }
}
