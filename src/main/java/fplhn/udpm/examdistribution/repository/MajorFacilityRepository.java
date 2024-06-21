package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.MajorFacility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorFacilityRepository extends JpaRepository<MajorFacility, String> {
}
