package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.core.student.examshift.model.response.SExamPaperShiftInfoAndPathResponse;
import fplhn.udpm.examdistribution.core.student.examshift.model.response.SExamPaperStartTimeEndTimeResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            select
            	eps.id as id,
            	ep.`path` as path,
            	ses.start_time as startTime,
            	ses.end_time as endTime,
            	etbs.allow_online as allowOnline,
            	eps.exam_shift_status as examShiftStatus
            from
            	exam_paper ep
            join exam_paper_shift eps on
            	ep.id = eps.id_exam_paper
            join exam_shift es on
            	eps.id_exam_shift = es.id
            join student_exam_shift ses on
            	ses.id_exam_shift = es.id
            join class_subject cs on
            	es.id_subject_class = cs.id
            join subject s on
            	cs.id_subject = s.id
            join exam_time_by_subject etbs on
            	s.id = etbs.id_subject
            where
            	es.exam_shift_code = :examShiftCode
            	and ses.id_student = :idStudent
            """, nativeQuery = true)
    SExamPaperShiftInfoAndPathResponse getExamPaperShiftInfoAndPathByExamShiftCode(String examShiftCode, String idStudent);

    @Query(value = """
            select
            	eps.id as id,
            	eps.start_time as startTime,
            	eps.end_time as endTime,
            	etbs.allow_online as allowOnline
            from
            	exam_paper ep
            join exam_paper_shift eps on
            	ep.id = eps.id_exam_paper
            join exam_shift es on
            	eps.id_exam_shift = es.id
            join class_subject cs on
            	es.id_subject_class = cs.id
            join subject s on
            	cs.id_subject = s.id
            join exam_time_by_subject etbs on
            	s.id = etbs.id_subject
            where
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    SExamPaperStartTimeEndTimeResponse getStartTimeEndTimeExamPaperByExamShiftCode(String examShiftCode);

}
