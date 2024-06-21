package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffRepository extends JpaRepository<Staff, String> {
}
