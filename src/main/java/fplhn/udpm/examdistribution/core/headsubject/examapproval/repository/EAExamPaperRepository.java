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

@Repository
public interface EAExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT ep.id AS id,
            	   ep.exam_paper_code AS examPaperCode,
            	   ep.exam_paper_type AS examPaperType,
            	   ep.exam_paper_status AS examPaperStatus,
            	   ep.path AS path,
            	   ep.created_exam_paper_date AS createdExamPaperDate,
            	   CONCAT(s2.subject_code, ' - ',s2.name) AS subjectName,
            	   s.staff_code AS staffUpload
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN staff s3 ON s3.id = s2.id_head_subject
            WHERE ep.exam_paper_status LIKE 'WAITING_APPROVAL'
            AND s3.id LIKE :idHeadSubject
            AND ep.status = 0
            AND (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE :#{"%" + #request.examPaperType + "%"})
            AND (:#{#request.idSubject} IS NULL OR s2.id LIKE :#{"%" + #request.idSubject + "%"})
            AND (:#{#request.staffUploadCode} IS NULL OR s.staff_code LIKE :#{"%" + #request.staffUploadCode + "%"})
            """,countQuery = """
            SELECT 	COUNT(ep.id)
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN staff s3 ON s3.id = s2.id_head_subject
            WHERE ep.exam_paper_status LIKE 'WAITING_APPROVAL'
            AND s3.id LIKE :idHeadSubject
            AND ep.status = 0
            AND (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE :#{"%" + #request.examPaperType + "%"})
            AND (:#{#request.idSubject} IS NULL OR s2.id LIKE :#{"%" + #request.idSubject + "%"})
            AND (:#{#request.staffUploadCode} IS NULL OR s.staff_code LIKE :#{"%" + #request.staffUploadCode + "%"})
            """
            , nativeQuery = true)
    Page<EAExamPaperResponse> getExamApprovals(Pageable pageable, EAExamPaperRequest request, String idHeadSubject);

    @Query(value = """
            SELECT ep.id as id,
            	   ep.path as path
			FROM exam_paper ep
			WHERE (ep.created_exam_paper_date + :dateTimeOf7Day) > :now
			AND ep.exam_paper_status LIKE 'WAITING_APPROVAL'
            """,nativeQuery = true)
    List<EAExamPaperCleanAfterSeventDayResponse> findAllExamPaperStatusAndCreatedDate(Long dateTimeOf7Day, Long now);

}