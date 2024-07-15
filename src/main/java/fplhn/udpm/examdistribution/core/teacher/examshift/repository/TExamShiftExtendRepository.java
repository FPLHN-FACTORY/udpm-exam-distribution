package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TExamShiftExtendRepository extends ExamShiftRepository {

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            WHERE es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    TExamShiftResponse getExamShiftByCode(String examShiftCode);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN block b ON
            	cs.id_block = b.id
            JOIN facility_child fc ON
            	cs.id_facility_child = fc.id
            WHERE
            	cs.id = :classSubjectId
            """, nativeQuery = true)
    Integer countByClassSubjectId(String classSubjectId);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            WHERE
            	es.exam_date = :examDate
            AND es.shift = :shift
            AND es.room = :room
            """, nativeQuery = true)
    Integer countByExamDateAndShiftAndRoom(Long examDate, String shift, String room);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
                ses.exam_student_status IN(0, 1, 2)
            	AND es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Integer countStudentInExamShift(String examShiftCode);

    @Query(value = """
            SELECT
            	ep.id
            FROM
            	exam_paper ep
            JOIN subject s ON
            	ep.id_subject = s.id
            JOIN department d ON
            	d.id = s.id_department
            JOIN department_facility df ON
            	d.id = df.id_department
            WHERE
            	df.id = :departmentFacilityId
            	AND ep.id_subject = :subjectId
            	AND ep.exam_paper_type = 'OFFICIAL_EXAM_PAPER'
            """, nativeQuery = true)
    List<String> getListIdExamPaper(String departmentFacilityId, String subjectId);
}