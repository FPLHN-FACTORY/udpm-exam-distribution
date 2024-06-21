package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamPaperRepository extends JpaRepository<ExamPaper, String> {
}
