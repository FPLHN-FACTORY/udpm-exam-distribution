package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.StudentPracticeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentPracticeRoomRepository extends JpaRepository<StudentPracticeRoom, String> {
}
