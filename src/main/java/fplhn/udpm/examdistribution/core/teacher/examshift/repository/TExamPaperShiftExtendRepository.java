package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamPaperShiftResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TExamPaperShiftExtendRepository extends ExamPaperShiftRepository {

    @Query(value = """
            SELECT
            	eps.id
            FROM
            	exam_paper_shift eps
            JOIN exam_shift es ON
            	eps.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    String findExamPaperShiftIdByExamShiftCode(String examShiftCode);

    @Query(value = """
                SELECT
                	eps.id as id,
                	eps.id_exam_shift as examShiftId,
                	eps.id_exam_paper as examPaperId,
                	eps.password as password
                FROM
                	exam_paper_shift eps
                JOIN exam_shift es ON
                	eps.id_exam_shift = es.id
                WHERE
                	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Optional<TExamPaperShiftResponse> findExamPaperShiftByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	etbs.exam_time
            FROM
            	exam_paper_shift eps
            JOIN exam_shift es ON
            	eps.id_exam_shift = es.id
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            JOIN exam_time_by_subject etbs ON
            	etbs.id_subject = s.id
            WHERE
                es.exam_shift_code = :examShiftCode
                AND etbs.id_facility = :facilityId
            """, nativeQuery = true)
    Long findExamTimeByExamShiftCode(String examShiftCode, String facilityId);

}
