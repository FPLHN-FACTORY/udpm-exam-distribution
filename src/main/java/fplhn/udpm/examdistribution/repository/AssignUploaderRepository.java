package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.AssignUploader;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignUploaderRepository extends JpaRepository<AssignUploader, String> {
}
