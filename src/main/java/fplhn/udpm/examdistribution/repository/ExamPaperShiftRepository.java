package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamPaperShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamPaperShiftRepository extends JpaRepository<ExamPaperShift, String> {
}
