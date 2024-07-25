package fplhn.udpm.examdistribution.core.student.trackstudent.repository;

import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.CheckRoomIsValidRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.InfoRoomRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.response.CheckExamShiftIsValidResponse;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.response.ExamShiftInfoResponse;
import fplhn.udpm.examdistribution.repository.StudentExamShiftTrackRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SESTStudentExamShiftTrackExtendRepository extends StudentExamShiftTrackRepository {

    @Query(value = """
            SELECT
            	st.email AS userName,
            	es.exam_shift_code AS roomCode,
            	cs.class_subject_code AS classSubject,
            	subj.name AS subjectName,
            	es.shift AS shift,
            	es.room AS roomName,
            	(
            	SELECT
            		s.account_fpt
            	FROM
            		staff s
            	WHERE
            		s.id = es.id_first_supervisor
                ) AS firstSupervisorName,
            	(
            	SELECT
            		s.account_fpt
            	FROM
            		staff s
            	WHERE
            		s.id = es.id_second_supervisor
                ) AS secondSupervisorName
            FROM
            	exam_shift es
            LEFT JOIN student_exam_shift ses ON
            	es.id = ses.id_exam_shift
            LEFT JOIN exam_paper_shift eps ON
                eps.id_exam_shift = es.id
            LEFT JOIN student st ON
            	st.id = ses.id_student
            LEFT JOIN class_subject cs ON
            	cs.id = es.id_subject_class
            LEFT JOIN subject subj ON
            	subj.id = cs.id_subject
            WHERE es.exam_shift_code = :#{#request.roomCode} AND
                  st.email = :#{#request.email} AND
                  cs.id_block = :blockId AND
                  (es.exam_shift_status = 'IN_PROGRESS')
            """, countQuery = """
            SELECT
            	COUNT(es.id)
            FROM
            	exam_shift es
            LEFT JOIN student_exam_shift ses ON
            	es.id = ses.id_exam_shift
            LEFT JOIN exam_paper_shift eps ON
                eps.id_exam_shift = es.id
            LEFT JOIN student st ON
            	st.id = ses.id_student
            LEFT JOIN class_subject cs ON
            	cs.id = es.id_subject_class
            LEFT JOIN subject subj ON
            	subj.id = cs.id_subject
            WHERE es.exam_shift_code = :#{#request.roomCode} AND
                  st.email = :#{#request.email} AND
                  cs.id_block = :blockId AND
                  (es.exam_shift_status = 'IN_PROGRESS')
            """, nativeQuery = true)
    CheckExamShiftIsValidResponse checkExamShiftIsValid(CheckRoomIsValidRequest request, String blockId);

    @Query(value = """
            SELECT
            	st.email AS userName,
            	es.exam_shift_code AS roomCode,
            	cs.class_subject_code AS classSubject,
            	subj.name AS subjectName,
            	es.shift AS shift,
            	es.room AS roomName,
            	es.exam_shift_status AS examShiftStatus,
            	(
            	SELECT
            		s.account_fpt
            	FROM
            		staff s
            	WHERE
            		s.id = es.id_first_supervisor
                ) AS firstSupervisorName,
            	(
            	SELECT
            		s.account_fpt
            	FROM
            		staff s
            	WHERE
            		s.id = es.id_second_supervisor
                ) AS secondSupervisorName
            FROM
            	exam_shift es
            LEFT JOIN student_exam_shift ses ON
            	es.id = ses.id_exam_shift
            LEFT JOIN exam_paper_shift eps ON
                eps.id_exam_shift = es.id
            LEFT JOIN student st ON
            	st.id = ses.id_student
            LEFT JOIN class_subject cs ON
            	cs.id = es.id_subject_class
            LEFT JOIN subject subj ON
            	subj.id = cs.id_subject
            WHERE es.exam_shift_code = :#{#request.roomCode} AND
                  st.email = :#{#request.email} AND
                  cs.id_block = :blockId
            """, countQuery = """
            SELECT
            	COUNT(es.id)
            FROM
            	exam_shift es
            LEFT JOIN student_exam_shift ses ON
            	es.id = ses.id_exam_shift
            LEFT JOIN exam_paper_shift eps ON
                eps.id_exam_shift = es.id
            LEFT JOIN student st ON
            	st.id = ses.id_student
            LEFT JOIN class_subject cs ON
            	cs.id = es.id_subject_class
            LEFT JOIN subject subj ON
            	subj.id = cs.id_subject
            WHERE es.exam_shift_code = :#{#request.roomCode} AND
                  st.email = :#{#request.email} AND
                  cs.id_block = :blockId
            """, nativeQuery = true)
    ExamShiftInfoResponse getExamShiftInfo(InfoRoomRequest request, String blockId);

}
