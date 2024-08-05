package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamTimeBySubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamTimeBySubjectRepository extends JpaRepository<ExamTimeBySubject, String> {
}
