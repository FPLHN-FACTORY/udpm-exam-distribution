package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.SubjectGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubjectGroupRepository extends JpaRepository<SubjectGroup, String> {
}
