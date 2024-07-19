package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.core.student.home.model.response.SExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SExamShiftExtendRepository extends ExamShiftRepository {

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode,
            	s.path_exam_rule as pathExamRule
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    SExamShiftResponse getExamShiftByCode(String examShiftCode);

}
