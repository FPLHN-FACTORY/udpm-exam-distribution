package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.core.student.home.model.response.StudentExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.ExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExamShiftStudentExtendRepository extends ExamShiftRepository {

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            WHERE es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    StudentExamShiftResponse getExamShiftByCode(String examShiftCode);

}
