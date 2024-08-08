package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.core.student.examshift.model.response.SStudentResponse;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.repository.StudentRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SStudentExtendRepository extends StudentRepository {

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
            	) AS isViolation,
                ses.leave_time AS leaveTime,
                ses.check_login AS checkLogin
            FROM student s
            JOIN student_exam_shift ses ON s.id = ses.id_student
            JOIN exam_shift es ON ses.id_exam_shift = es.id
            JOIN class_subject cs ON cs.id = es.id_subject_class
            WHERE
                ses.exam_student_status IN(0, 1, 2)
            	AND es.exam_shift_code = :examShiftCode
                AND cs.id_block = :blockId
            """,countQuery = """
            SELECT
                COUNT(s.id)
            FROM student s
            JOIN student_exam_shift ses ON s.id = ses.id_student
            JOIN exam_shift es ON ses.id_exam_shift = es.id
            JOIN class_subject cs ON cs.id = es.id_subject_class
            WHERE
                ses.exam_student_status IN(0, 1, 2)
            	AND es.exam_shift_code = :examShiftCode
                AND cs.id_block = :blockId
            """, nativeQuery = true)
    List<SStudentResponse> findAllStudentByExamShiftCode(String examShiftCode, String blockId);

    @Query(value = """
            SELECT
             s.id as id,
             s.name as name,
             s.student_code as studentCode,
             s.email as email,
             s.picture as picture,
             ses.join_time as joinTime,
             ses.start_time as startTime,
             ses.end_time as endTime,
             eps.start_time as examPaperShiftStartTime,
             eps.end_time as examPaperShiftEndTime
            FROM
            	student s
            JOIN student_exam_shift ses ON
            	s.id = ses.id_student
            LEFT JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            LEFT JOIN exam_paper_shift eps on
             	es.id = ses.id_exam_shift
            WHERE
                ses.exam_student_status = 4
            	AND es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    List<SStudentResponse> findAllStudentRejoinByExamShiftCode(String examShiftCode);

    @Query("""
            SELECT st
            FROM Student st
            WHERE st.id = :studentId AND
                  st.status = 0
            """)
    Optional<Student> findStudentExist(String studentId);

}
