package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.entity.ExamTimeBySubject;
import fplhn.udpm.examdistribution.repository.ExamTimeBySubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TExamTimeBySubjectExtendRepository extends ExamTimeBySubjectRepository {

    @Query(value = """
            SELECT 
            	etbs.percent_random
            FROM 
            	exam_time_by_subject etbs
            WHERE 
            	etbs.id_facility = :facilityId
            	and etbs.id_subject = :subjectId
            """, nativeQuery = true)
    Long getPercentRandom(String facilityId, String subjectId);

}
