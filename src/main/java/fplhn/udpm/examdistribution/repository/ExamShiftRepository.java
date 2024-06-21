package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamShiftRepository extends JpaRepository<ExamShift, String> {
}
