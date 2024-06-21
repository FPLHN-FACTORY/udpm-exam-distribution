package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepartmentFacilityRepository extends JpaRepository<DepartmentFacility, String> {
}
