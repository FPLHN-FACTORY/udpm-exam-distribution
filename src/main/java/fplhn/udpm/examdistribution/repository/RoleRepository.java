package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, String> {
}
