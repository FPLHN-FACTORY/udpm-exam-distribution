package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamShiftRepository extends JpaRepository<StudentExamShift, String> {
}
