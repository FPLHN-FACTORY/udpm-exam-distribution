package fplhn.udpm.examdistribution.core.teacher.trackhistory.repository;

import fplhn.udpm.examdistribution.core.teacher.trackhistory.model.response.ListStudentMakeMistakeResposne;
import fplhn.udpm.examdistribution.repository.StudentExamShiftTrackRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TTrackHistoryRepository extends StudentExamShiftTrackRepository {

    @Query(value = """
            SELECT  sest.url AS url,
                    sest.time_violation AS timeViolation
            FROM student_exam_shift_track sest
            JOIN student st ON st.id = sest.id_student
            JOIN exam_shift es ON es.id = sest.id_exam_shift
            JOIN class_subject cs ON cs.id = es.id_subject_class
            WHERE es.exam_shift_code = :examShiftCode AND
                  cs.id_block = :blockId AND 
                  st.id = :studentId
            """, nativeQuery = true)
    List<ListStudentMakeMistakeResposne> getListStudentMakeMistake(String examShiftCode, String studentId, String blockId);

}
