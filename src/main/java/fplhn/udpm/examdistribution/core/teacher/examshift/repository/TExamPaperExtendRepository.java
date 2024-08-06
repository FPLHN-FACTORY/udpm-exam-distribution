package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamPaperShiftInfoAndPathResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT
                eps.id as id,
            	ep.`path` as path,
             	eps.start_time as startTime,
             	eps.end_time as endTime
            FROM
            	exam_paper ep
            JOIN exam_paper_shift eps ON
            	ep.id = eps.id_exam_paper
            JOIN exam_shift es ON
            	eps.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    TExamPaperShiftInfoAndPathResponse getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    @Query("""
            SELECT ep
            FROM ExamPaper ep
            WHERE ep.path = :path
            """)
    Optional<ExamPaper> findExamPaperByPath(String path);

}
