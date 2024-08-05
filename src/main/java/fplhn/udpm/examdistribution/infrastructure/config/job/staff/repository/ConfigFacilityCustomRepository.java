package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConfigFacilityCustomRepository extends FacilityRepository {

    Optional<Facility> findByCode(String facilityCode);

}
