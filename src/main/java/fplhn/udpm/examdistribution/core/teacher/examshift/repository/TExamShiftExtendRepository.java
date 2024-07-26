package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.TExamShiftResponse;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.response.THeadSubjectAndContentSendMailResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
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
            	s.name as subjectName,
            	ep.`path` as pathExamPaper,
            	s2.account_fe as accountFe,
            	s2.account_fpt as accountFpt
            FROM
            	exam_shift es
            JOIN exam_paper_shift eps ON
            	es.id = eps.id_exam_shift
            JOIN exam_paper ep ON
            	ep.id = eps.id_exam_paper
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            JOIN subject_by_subject_group sbsg ON
             	sbsg.id_subject = s.id
            JOIN subject_group sg ON
            	sbsg.id_subject_group = sg.id
            JOIN head_subject_by_semester hsbs ON
            	sg.id = hsbs.id_subject_group
            JOIN staff s2 ON
            	hsbs.id_staff = s2.id
            WHERE
            	es.exam_shift_code = :examShiftCode
                    """, nativeQuery = true)
    THeadSubjectAndContentSendMailResponse getHeadSubjectAndContentSendMail(String examShiftCode);

}
