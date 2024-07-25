package fplhn.udpm.examdistribution.core.student.trackstudent.repository;

import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SESTExamShiftExtendRepository extends ExamShiftRepository {

    Optional<ExamShift> getExamShiftByExamShiftCode(String room);

    @Query("""
            SELECT es
            FROM ExamShift es
            WHERE es.examShiftCode = :examShiftCode AND
                  es.classSubject.facilityChild.facility.id = :facilityId AND
                  es.classSubject.block.semester.id = :semesterId AND
                  es.examShiftStatus = 'IN_PROGRESS'
            """)
    Optional<ExamShift> getExamShiftByExamShiftCodeInProgress(String examShiftCode, String facilityId, String semesterId);

}
