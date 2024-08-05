package fplhn.udpm.examdistribution.core.headsubject.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDExamPaperStartTimeEndTimeResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSExamPaperShiftInfoAndPathResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSExamPaperStartTimeEndTimeResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HSExamPaperExtendRepository extends ExamPaperRepository {

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
    HSExamPaperShiftInfoAndPathResponse getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
                eps.id as id,
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
    HSExamPaperStartTimeEndTimeResponse getStartTimeEndTimeExamPaperByExamShiftCode(String examShiftCode);

}
