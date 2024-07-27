package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TExamFileRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TCountExamPaperByStatus;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TExamFileResponse;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TSampleExamPaperResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT COUNT(ep.id) FROM exam_paper ep
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN assign_uploader au ON s2.id = au.id_subject
            WHERE au.id_staff LIKE :staffId
            AND s2.id = :subjectId
            """, nativeQuery = true)
    Integer getCountUploaded(String staffId, String subjectId);

    @Query(value = """
            SELECT ep.path AS path,
                   ep.id AS id
            FROM exam_paper ep
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN department_facility df ON df.id = mf.id_department_facility
            JOIN subject s ON s.id = ep.id_subject
            WHERE df.id = :departmentFacilityId
            AND ep.exam_paper_type LIKE 'SAMPLE_EXAM_PAPER'
            AND s.id = :subjectId
            """, nativeQuery = true)
    List<TSampleExamPaperResponse> getSampleExamPaper(String departmentFacilityId, String subjectId);

    @Query(value = """
            SELECT ep.id AS id,
                   ep.exam_paper_code AS examPaperCode,
                   ep.exam_paper_type AS examPaperType,
                   ep.exam_paper_status AS examPaperStatus,
                   ep.path AS path,
                   ep.created_exam_paper_date AS createdExamPaperDate,
                   CONCAT(s.staff_code,' - ',s.name) AS staffUpload
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            WHERE (:#{#request.idSubject} IS NULL OR s2.id LIKE :#{#request.idSubject})
            AND s.id = :staffId
            AND (:#{#request.examPaperStatus} IS NULL OR ep.exam_paper_status = :#{#request.examPaperStatus})
            AND (ep.exam_paper_type != 'SAMPLE_EXAM_PAPER' OR ep.exam_paper_type IS NULL)
            AND ((:#{#request.codeAndTeacher} IS NULL)
                OR (ep.exam_paper_code LIKE :#{"%" + #request.codeAndTeacher + "%"})
                OR CONCAT(s.staff_code,' - ',s.name)  LIKE :#{"%" + #request.codeAndTeacher + "%"})
            AND ((:#{#request.startDate} IS NULL)
                OR (:#{#request.endDate} IS NULL)
                OR ((ep.created_exam_paper_date BETWEEN :#{#request.startDate} AND :#{#request.endDate})))
            """, countQuery = """
            SELECT COUNT(ep.id)
             FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            WHERE (:#{#request.idSubject} IS NULL OR s2.id LIKE :#{#request.idSubject})
            AND s.id = :staffId
            AND (:#{#request.examPaperStatus} IS NULL OR ep.exam_paper_status = :#{#request.examPaperStatus})
            AND (ep.exam_paper_type != 'SAMPLE_EXAM_PAPER' OR ep.exam_paper_type IS NULL)
            AND ((:#{#request.codeAndTeacher} IS NULL)
                OR (ep.exam_paper_code LIKE :#{"%" + #request.codeAndTeacher + "%"})
                OR CONCAT(s.staff_code,' - ',s.name)  LIKE :#{"%" + #request.codeAndTeacher + "%"})
            AND ((:#{#request.startDate} IS NULL)
                OR (:#{#request.endDate} IS NULL)
                OR ((ep.created_exam_paper_date BETWEEN :#{#request.startDate} AND :#{#request.endDate})))
            """, nativeQuery = true)
    Page<TExamFileResponse> getExamPapers(Pageable pageable, TExamFileRequest request, String staffId);

    @Query(value = """
            SELECT ep.path AS path
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            WHERE s.id = :staffId
            AND ep.id = :examPaperId
            """, nativeQuery = true)
    Optional<String> getExamPaper(String examPaperId, String staffId);

    @Query(value = """ 
            SELECT
                SUM(CASE WHEN ep.exam_paper_status = 'IN_USE' THEN 1 ELSE 0 END) AS inUse,
                SUM(CASE WHEN ep.exam_paper_status = 'WAITING_APPROVAL' THEN 1 ELSE 0 END) AS waitingApproval,
                SUM(CASE WHEN ep.exam_paper_status = 'REJECTED' THEN 1 ELSE 0 END) AS rejected
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            WHERE s2.id LIKE :subjectId
            AND s.id = :staffId
            AND (ep.exam_paper_type != 'SAMPLE_EXAM_PAPER' OR ep.exam_paper_type IS NULL)
                """, nativeQuery = true)
    Optional<TCountExamPaperByStatus> countExamPaper(String subjectId, String staffId);

}
