package fplhn.udpm.examdistribution.core.headsubject.examapproval.repository;

import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.request.EAExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EAExamPaperCleanAfterSeventDayResponse;
import fplhn.udpm.examdistribution.core.headsubject.examapproval.model.response.EAExamPaperResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EAExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT ep.id AS id,
            	   ep.exam_paper_code AS examPaperCode,
            	   ep.exam_paper_status AS examPaperStatus,
            	   ep.path AS path,
            	   ep.created_exam_paper_date AS createdExamPaperDate,
            	   CONCAT(subj.subject_code, ' - ',subj.name) AS subjectName,
            	   CONCAT(s.staff_code,' - ',s.name) AS staffUpload
            FROM exam_paper ep
            JOIN subject_by_subject_group sbsg ON sbsg.id_subject = ep.id_subject
            JOIN subject subj ON subj.id = sbsg.id_subject
            JOIN subject_group sg ON sg.id = sbsg.id_subject_group
            JOIN head_subject_by_semester hsbs ON hsbs.id_subject_group = sg.id
            JOIN block b ON b.id = ep.id_block
            JOIN staff s ON s.id = hsbs.id_staff
            WHERE ep.exam_paper_status = 'WAITING_APPROVAL' AND
                  hsbs.id_staff = :idHeadSubject AND
                  sg.id_department_facility = :departmentFacilityId AND
                  hsbs.id_semester = :semesterId AND
                  b.id_semester = :semesterId AND
                  ep.status = 0 AND 
                  (:#{#request.idSubject} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.idSubject}) ,'%') )AND
                  (:#{#request.staffUploadCode} IS NULL OR CONCAT(s.staff_code,' - ',s.name) LIKE CONCAT('%', TRIM(:#{#request.staffUploadCode}) ,'%'))
            """,countQuery = """
            SELECT COUNT(ep.id)
            FROM exam_paper ep
            JOIN subject_by_subject_group sbsg ON sbsg.id_subject = ep.id_subject
            JOIN subject subj ON subj.id = sbsg.id_subject
            JOIN subject_group sg ON sg.id = sbsg.id_subject_group
            JOIN head_subject_by_semester hsbs ON hsbs.id_subject_group = sg.id
            JOIN block b ON b.id = ep.id_block
            JOIN staff s ON s.id = hsbs.id_staff
            WHERE ep.exam_paper_status = 'WAITING_APPROVAL' AND
                  hsbs.id_staff = :idHeadSubject AND
                  sg.id_department_facility = :departmentFacilityId AND
                  hsbs.id_semester = :semesterId AND
                  b.id_semester = :semesterId AND
                  ep.status = 0 AND 
                  (:#{#request.idSubject} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.idSubject}) ,'%') )AND
                  (:#{#request.staffUploadCode} IS NULL OR CONCAT(s.staff_code,' - ',s.name) LIKE CONCAT('%', TRIM(:#{#request.staffUploadCode}) ,'%'))
            """
            , nativeQuery = true)
    Page<EAExamPaperResponse> getExamApprovals(Pageable pageable, EAExamPaperRequest request, String idHeadSubject, String semesterId, String departmentFacilityId);

    @Query(value = """
            SELECT ep.id as id,
            	   ep.path as path
			FROM exam_paper ep
			WHERE (ep.created_exam_paper_date + :dateTimeOf7Day) > :now
			AND ep.exam_paper_status LIKE 'WAITING_APPROVAL'
            """,nativeQuery = true)
    List<EAExamPaperCleanAfterSeventDayResponse> findAllExamPaperStatusAndCreatedDate(Long dateTimeOf7Day, Long now);

    Optional<ExamPaper> findByPath(String fileId);

}
