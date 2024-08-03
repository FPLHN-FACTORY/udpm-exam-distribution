package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.repository.StudentPracticeRoomRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface TMEPStudentPracticeRoomRepository extends StudentPracticeRoomRepository {

    @Transactional
    @Modifying
    @Query(value = """
            DELETE FROM student_practice_room 
                   WHERE id_practice_room = :practiceRoomId
            """, nativeQuery = true)
    void deleteByPracticeRoomId(@Param("practiceRoomId") String practiceRoomId);

}
