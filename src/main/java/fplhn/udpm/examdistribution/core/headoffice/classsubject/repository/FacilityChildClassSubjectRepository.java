package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityChildClassSubjectRepository extends FacilityChildRepository {

    List<FacilityChild> findAllByFacilityAndStatus(Facility facility, EntityStatus status);
}
