package fplhn.udpm.examdistribution.core.student.practiceroom.repository;

import fplhn.udpm.examdistribution.entity.PracticeRoom;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.PracticeRoomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPracticeRoomRepository extends PracticeRoomRepository {

    List<PracticeRoom> findAllByPracticeRoomCodeAndPasswordAndStatusAndFacility_Id(String code, String password, EntityStatus status, String facility);

}
