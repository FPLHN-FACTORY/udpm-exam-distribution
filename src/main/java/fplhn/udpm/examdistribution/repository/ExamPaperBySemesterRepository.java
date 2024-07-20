package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamPaperBySemester;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamPaperBySemesterRepository extends JpaRepository<ExamPaperBySemester, String> {
}
