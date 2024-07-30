package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamClassification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamClassificationRepository extends JpaRepository<ExamClassification, String> {
}
