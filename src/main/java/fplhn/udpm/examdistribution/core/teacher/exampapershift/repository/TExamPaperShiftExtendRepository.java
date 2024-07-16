package fplhn.udpm.examdistribution.core.teacher.exampapershift.repository;

import fplhn.udpm.examdistribution.core.teacher.exampapershift.model.response.TExamPaperShiftResponse;
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
                	eps.id_exam_paper as examPaperId
                FROM
                	exam_paper_shift eps
                JOIN exam_shift es ON
                	eps.id_exam_shift = es.id
                WHERE
                	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Optional<TExamPaperShiftResponse> findExamPaperShiftByExamShiftCode(String examShiftCode);

}
