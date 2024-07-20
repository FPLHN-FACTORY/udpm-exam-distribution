package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.SubjectBySubjectGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectBySubjectGroupRepository extends JpaRepository<SubjectBySubjectGroup, String> {
}
