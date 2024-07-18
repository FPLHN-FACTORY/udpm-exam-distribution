package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffMajorFacilityRepository extends JpaRepository<StaffMajorFacility, String> {
}
