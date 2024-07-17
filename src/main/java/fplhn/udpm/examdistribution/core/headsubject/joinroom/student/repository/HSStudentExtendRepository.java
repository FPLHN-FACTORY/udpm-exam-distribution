package fplhn.udpm.examdistribution.core.headsubject.joinroom.student.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.student.model.response.HSStudentResponse;
import fplhn.udpm.examdistribution.repository.StudentRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HSStudentExtendRepository extends StudentRepository {

    @Query(value = """
            SELECT
                s.id AS id,
            	s.name AS name,
            	s.student_code AS studentCode,
            	s.email AS email,
            	ses.join_time AS joinTime,
            	s.picture AS picture,
            	(
            	    SELECT
            	        CASE
            	            WHEN COUNT(sest.id > 0) THEN 0
            	            ELSE 1
            	        END
            	    FROM student_exam_shift_track sest
            	    WHERE sest.id_student = s.id
            	) AS isViolation
            FROM student s
            JOIN student_exam_shift ses ON s.id = ses.id_student
            JOIN exam_shift es ON ses.id_exam_shift = es.id
            JOIN class_subject cs ON cs.id = es.id_subject_class
            WHERE es.exam_shift_code = :examShiftCode AND
                  cs.id_block = :blockId
            """, countQuery = """
            SELECT
                COUNT(s.id)
            FROM student s
            JOIN student_exam_shift ses ON s.id = ses.id_student
            JOIN exam_shift es ON ses.id_exam_shift = es.id
            JOIN class_subject cs ON cs.id = es.id_subject_class
            WHERE es.exam_shift_code = :examShiftCode AND
                  cs.id_block = :blockId
            """, nativeQuery = true)
    List<HSStudentResponse> findAllStudentByExamShiftCode(String examShiftCode, String blockId);

}
