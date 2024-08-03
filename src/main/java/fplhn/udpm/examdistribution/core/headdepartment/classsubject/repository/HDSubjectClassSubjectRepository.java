package fplhn.udpm.examdistribution.core.headdepartment.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDSubjectClassSubjectRepository extends SubjectRepository {

    Optional<Subject> findBySubjectCode(String subjectCode);

}
