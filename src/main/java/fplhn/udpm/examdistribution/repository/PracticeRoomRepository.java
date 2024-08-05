package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.PracticeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PracticeRoomRepository extends JpaRepository<PracticeRoom, String> {
}
