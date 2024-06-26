package fplhn.udpm.examdistribution.core.headoffice.staff.service.impl;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleChangePermissionRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.repository.HOStaffRoleRepository;
import fplhn.udpm.examdistribution.core.headoffice.staff.service.HOStaffRoleService;
import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.RoleRepository;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import fplhn.udpm.examdistribution.utils.Helper;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HOStaffRoleServiceImpl implements HOStaffRoleService {

    private final HOStaffRoleRepository staffRoleRepository;

    private final HOStaffRepository staffRepository;

    private final RoleRepository roleRepository;

    public HOStaffRoleServiceImpl(HOStaffRoleRepository staffRoleRepository, HOStaffRepository staffRepository, RoleRepository roleRepository) {
        this.staffRoleRepository = staffRoleRepository;
        this.staffRepository = staffRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public ResponseObject<?> getAllRole(String staffId) {
        return new ResponseObject<>(staffRoleRepository.getRolesByStaffId(staffId),
                HttpStatus.OK,
                "Get roles by staff id successfully!");
    }

    @Override
    public ResponseObject<?> getRolesChecked(HOStaffRoleRequest roleRequest) {
        Pageable page = Helper.createPageable(roleRequest, "createdDate");
        return new ResponseObject<>(staffRoleRepository.getRolesChecked(page, roleRequest), HttpStatus.OK, "get roles checked successfully");
    }

    @Override
    public ResponseObject<?> updateStaffRole(HOStaffRoleChangePermissionRequest request) {
        Optional<Role> role = roleRepository.findById(request.getIdRole());
        Optional<Staff> staff = staffRepository.findById(request.getIdStaff());
        if (role.isEmpty() || staff.isEmpty()) {
            return new ResponseObject<>(null, HttpStatus.NOT_FOUND, "Staff or role not found");
        }
        List<StaffRole> staffRoles = staffRoleRepository.findAllByRole_IdAndStaff_Id(request.getIdRole(), request.getIdStaff());
        if (staffRoles.isEmpty()) {
            StaffRole staffRole = new StaffRole();
            staffRole.setRole(role.get());
            staffRole.setStaff(staff.get());
            staffRole.setId(CodeGenerator.generateRandomCode());
            staffRole.setStatus(EntityStatus.ACTIVE);
            System.out.println("add staff role " );
            staffRoleRepository.save(staffRole);
        } else {
            staffRoles.get(0).setStatus(staffRoles.get(0).getStatus().equals(EntityStatus.INACTIVE) ? EntityStatus.ACTIVE : EntityStatus.INACTIVE);
            staffRoleRepository.save(staffRoles.get(0));
        }
        return new ResponseObject<>(null, HttpStatus.OK, "change permission successfully");
    }

}
