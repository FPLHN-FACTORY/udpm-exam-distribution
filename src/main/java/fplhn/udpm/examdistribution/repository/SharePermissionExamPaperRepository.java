package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.SharePermissionExamPaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SharePermissionExamPaperRepository extends JpaRepository<SharePermissionExamPaper, String> {
}
