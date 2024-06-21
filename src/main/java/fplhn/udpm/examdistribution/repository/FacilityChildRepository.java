package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.FacilityChild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FacilityChildRepository extends JpaRepository<FacilityChild, String> {
}
