package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.core.student.examshift.model.response.SExamShiftResponse;
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
            	s.name as subjectName,
            	cs.class_subject_code as classSubjectCode,
            	er.file_id as pathExamRule
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            JOIN exam_rule er ON
                s.id_exam_rule = er.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    SExamShiftResponse getExamShiftByCode(String examShiftCode);

    @Query("""
            SELECT es
            FROM ExamShift es
            WHERE es.examShiftCode = :examShiftCode AND
                  es.classSubject.block.semester.id = :semesterId AND
                  es.classSubject.facilityChild.facility.id = :facilityId
            """)
    Optional<ExamShift> findExamShiftByCode(String examShiftCode,String semesterId,String facilityId);

}
