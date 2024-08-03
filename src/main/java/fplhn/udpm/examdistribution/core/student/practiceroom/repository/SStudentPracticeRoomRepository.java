package fplhn.udpm.examdistribution.core.student.practiceroom.repository;

import fplhn.udpm.examdistribution.entity.StudentPracticeRoom;
import fplhn.udpm.examdistribution.repository.StudentPracticeRoomRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SStudentPracticeRoomRepository extends StudentPracticeRoomRepository {

    List<StudentPracticeRoom> findAllByStudent_IdAndPracticeRoom_Id(String studentId, String practiceRoomId);

}
