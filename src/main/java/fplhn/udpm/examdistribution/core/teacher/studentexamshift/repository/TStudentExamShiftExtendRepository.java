package fplhn.udpm.examdistribution.core.teacher.studentexamshift.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.repository.StudentExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TStudentExamShiftExtendRepository extends StudentExamShiftRepository {

    Optional<StudentExamShift> findByExamShiftIdAndStudentId(String examShiftId, String studentId);

    @Query(value = """
            SELECT
            	ses.id
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            	AND ses.exam_student_status = 0
                        """, nativeQuery = true)
    List<String> findAllStudentExamShiftIdByExamShiftCode(String examShiftCode);

}
