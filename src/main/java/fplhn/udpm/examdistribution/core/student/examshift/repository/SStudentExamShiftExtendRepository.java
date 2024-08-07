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
            WHERE
                ses.examShift.examShiftCode = :examShiftCode AND
                ses.student.id = :studentId
            """)
    Optional<StudentExamShift> findByExamShiftCodeAndStudentId(String examShiftCode, String studentId);

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

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
                ses.exam_student_status IN(0, 1, 2)
            	AND es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Integer countStudentInExamShift(String examShiftCode);

}
