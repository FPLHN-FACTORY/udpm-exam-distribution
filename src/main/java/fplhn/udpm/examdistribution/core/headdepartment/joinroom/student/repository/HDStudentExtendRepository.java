package fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.student.model.response.HDStudentResponse;
import fplhn.udpm.examdistribution.repository.StudentRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDStudentExtendRepository extends StudentRepository {

    @Query(value = """
            SELECT
                s.id as id,
            	s.name as name,
            	s.student_code as studentCode,
            	s.email as email,
            	ses.join_time as joinTime
            FROM
            	student s
            JOIN student_exam_shift ses
                ON
            	s.id = ses.id_student
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    List<HDStudentResponse> findAllStudentByExamShiftCode(String examShiftCode);

}
