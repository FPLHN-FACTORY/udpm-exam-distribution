package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {
}
