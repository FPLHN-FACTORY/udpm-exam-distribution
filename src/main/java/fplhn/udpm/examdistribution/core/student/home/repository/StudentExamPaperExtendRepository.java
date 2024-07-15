package fplhn.udpm.examdistribution.core.student.home.repository;

import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT
            	ep.`path`
            FROM
            	exam_paper ep
            JOIN exam_paper_shift eps ON
            	ep.id = eps.id_exam_paper
            JOIN exam_shift es ON
            	eps.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    String getPathByExamShiftCode(String examShiftCode);

}
