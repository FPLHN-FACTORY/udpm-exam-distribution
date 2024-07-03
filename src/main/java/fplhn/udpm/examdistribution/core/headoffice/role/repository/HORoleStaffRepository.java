package fplhn.udpm.examdistribution.core.headoffice.role.repository;

import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRoleRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HORoleStaffRepository extends StaffRoleRepository {

    List<StaffRole> findAllByRole_IdAndStatus(String roleId, EntityStatus status);

}
