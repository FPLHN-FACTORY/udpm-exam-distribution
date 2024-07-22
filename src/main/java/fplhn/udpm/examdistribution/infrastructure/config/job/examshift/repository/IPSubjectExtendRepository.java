package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPSubjectExtendRepository extends SubjectRepository {

    Optional<Subject> findByNameAndSubjectCode(String name, String subjectCode);

}
