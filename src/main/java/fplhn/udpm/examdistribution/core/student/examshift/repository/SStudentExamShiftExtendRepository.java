package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.repository.StudentExamShiftRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SStudentExamShiftExtendRepository extends StudentExamShiftRepository {

    Optional<StudentExamShift> findByExamShiftIdAndStudentId(String examShiftId, String studentId);

    @Query("""
            SELECT ses
            FROM StudentExamShift ses
            WHERE ses.examShift.examShiftCode = :examShiftCode AND
                  ses.student.id = :studentId AND
                  ses.examShift.classSubject.facilityChild.facility.id = :facilityId AND
                  ses.examShift.classSubject.block.semester.id = :semesterId
            """)
    Optional<StudentExamShift> findByExamShiftCodeAndStudentIdAndFacilityId(
            String examShiftCode,
            String studentId,
            String facilityId,
            String semesterId
    );

}
