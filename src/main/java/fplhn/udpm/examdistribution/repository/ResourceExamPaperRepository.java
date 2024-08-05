package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ResourceExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResourceExamPaperRepository extends JpaRepository<ResourceExamPaper, String> {
}
