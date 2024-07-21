package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPFacilityChildRepository extends FacilityChildRepository {

    Optional<FacilityChild> findByName(String name);

}
