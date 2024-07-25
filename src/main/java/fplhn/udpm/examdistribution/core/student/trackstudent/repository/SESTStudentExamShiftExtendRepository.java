package fplhn.udpm.examdistribution.core.student.trackstudent.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.repository.StudentExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SESTStudentExamShiftExtendRepository extends StudentExamShiftRepository {

    @Query("""
            SELECT ses
            FROM StudentExamShift ses
            WHERE ses.student.email = :email AND
                  ses.examShift.id = :examShiftId
            """)
    Optional<StudentExamShift> findByEmailAndExamShiftId(String email, String examShiftId);

}
