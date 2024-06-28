package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubjectClassSubjectRepository extends SubjectRepository {

    Optional<Subject> findBySubjectCode(String subjectCode);

}
