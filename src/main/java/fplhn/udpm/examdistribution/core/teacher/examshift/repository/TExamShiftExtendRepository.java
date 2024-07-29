package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TSendMailToSupervisorWhenOpenExamPaperResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TExamShiftExtendRepository extends ExamShiftRepository {

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode,
            	s.path_exam_rule as pathExamRule
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    TExamShiftResponse getExamShiftByCode(String examShiftCode);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

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

    @Query(value = """
            SELECT
            	es.exam_shift_code as examShiftCode,
            	es.room as room,
            	es.exam_date as examDate,
            	es.shift as shift,
            	cs.class_subject_code as classSubjectCode,
            	s3.name as subjectName,
            	s1.name as nameFirstSupervisor,
            	s1.staff_code as codeFirstSupervisor,
            	s2.name as nameSecondSupervisor,
            	s2.staff_code as codeSecondSupervisor,
            	ep.`path` as pathExamPaper,
            	s4.account_fe as accountFeHeadSubject,
            	s4.account_fpt as accountFptHeadSubject
            FROM
            	exam_shift es
            JOIN staff s1 ON
            	es.id_first_supervisor = s1.id
            JOIN staff s2 ON
            	es.id_second_supervisor = s2.id
            JOIN exam_paper_shift eps ON
            	es.id = eps.id_exam_shift
            JOIN exam_paper ep ON
            	ep.id = eps.id_exam_paper
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s3 ON
            	cs.id_subject = s3.id
            JOIN subject_by_subject_group sbsg ON
            	sbsg.id_subject = s3.id
            JOIN subject_group sg ON
            	sbsg.id_subject_group = sg.id
            JOIN head_subject_by_semester hsbs ON
            	sg.id = hsbs.id_subject_group
            JOIN staff s4 ON
            	hsbs.id_staff = s4.id
            WHERE
                es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    THeadSubjectAndContentSendMailResponse getHeadSubjectAndContentSendMail(String examShiftCode);

    @Query(value = """
            SELECT
            	es.exam_shift_code as examShiftCode,
            	es.room as room,
            	es.exam_date as examDate,
            	es.shift as shift,
            	cs.class_subject_code as classSubjectCode,
            	s3.name as subjectName,
            	s1.account_fe as accountFeFirstSupervisor,
            	s1.account_fpt as accountFptFirstSupervisor,
            	s2.account_fe as accountFeSecondSupervisor,
            	s2.account_fpt as accountFptSecondSupervisor,
            	ep.`path` as pathExamPaper
            FROM
            	exam_shift es
            JOIN staff s1 ON
            	es.id_first_supervisor = s1.id
            JOIN staff s2 ON
            	es.id_second_supervisor = s2.id
            JOIN exam_paper_shift eps ON
            	es.id = eps.id_exam_shift
            JOIN exam_paper ep ON
            	ep.id = eps.id_exam_paper
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s3 ON
            	cs.id_subject = s3.id
            """, nativeQuery = true)
    TSendMailToSupervisorWhenOpenExamPaperResponse sendMailToSupervisorWhenOpenExamPaper(String examShiftCode);

}
