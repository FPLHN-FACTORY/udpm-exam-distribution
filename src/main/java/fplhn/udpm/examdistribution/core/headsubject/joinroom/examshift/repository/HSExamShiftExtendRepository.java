package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response.HSExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.ExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HSExamShiftExtendRepository extends ExamShiftRepository {

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            WHERE es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    HSExamShiftResponse getExamShiftByCode(String examShiftCode);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Integer countStudentInExamShift(String examShiftCode);

}
