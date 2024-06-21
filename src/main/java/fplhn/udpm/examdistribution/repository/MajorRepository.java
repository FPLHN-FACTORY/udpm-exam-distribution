package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.Major;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MajorRepository extends JpaRepository<Major, String> {
}
