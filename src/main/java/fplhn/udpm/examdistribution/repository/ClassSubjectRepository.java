package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassSubjectRepository extends JpaRepository<ClassSubject, String> {
}
