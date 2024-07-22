package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPClassSubjectExtendRepository extends ClassSubjectRepository {

    Optional<ClassSubject> findBySubjectAndBlockAndClassSubjectCodeAndFacilityChild(
            Subject subject,
            Block block,
            String classSubjectCode,
            FacilityChild facilityChild
    );

}
