package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StaffSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffSubjectRepository extends JpaRepository<StaffSubject, String> {
}
