package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StaffDepartmentFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffDepartmentFacilityRepository extends JpaRepository<StaffDepartmentFacility, String> {
}
