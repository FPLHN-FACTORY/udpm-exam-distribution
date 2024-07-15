package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShiftTrack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamShiftTrackRepository extends JpaRepository<StudentExamShiftTrack,String> {
}
