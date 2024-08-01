package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.ExamRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExamRuleRepository extends JpaRepository<ExamRule, String> {
}
