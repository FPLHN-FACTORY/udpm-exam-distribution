package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StaffRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRoleRepository extends JpaRepository<StaffRole, String> {
}
