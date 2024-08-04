package fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository;

import fplhn.udpm.examdistribution.entity.ExamTimeBySubject;
import fplhn.udpm.examdistribution.repository.ExamTimeBySubjectRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SRExamTimeBySubjectExtendRepository extends ExamTimeBySubjectRepository {

    @Query("""
            SELECT etbs
            FROM ExamTimeBySubject etbs
            WHERE
                etbs.facility.id = :facilityId AND
                etbs.subject.id = :subjectId AND
                etbs.status = 0
            """)
    Optional<ExamTimeBySubject> findExamTimeBySubjectIdAndFacilityId(String subjectId, String facilityId);

}
