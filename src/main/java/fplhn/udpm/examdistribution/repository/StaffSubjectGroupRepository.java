package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StaffSubjectGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffSubjectGroupRepository extends JpaRepository<StaffSubjectGroup, String> {
}
