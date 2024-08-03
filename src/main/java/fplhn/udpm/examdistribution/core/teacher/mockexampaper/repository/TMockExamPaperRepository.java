package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMockExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TMockExamPaperResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TMockExamPaperRepository extends ExamPaperRepository {

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
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN department_facility df ON df.id = mf.id_department_facility
            WHERE ep.exam_paper_type LIKE 'MOCK_EXAM_PAPER'
            AND ep.id_block = :#{#request.blockId}
            AND ep.is_public = 1
            AND df.id_facility = :idFacility
            AND (:#{#request.idSubject} IS NULL OR s2.id LIKE :#{#request.idSubject})
            AND ((:#{#request.codeAndTeacher} IS NULL) 
                     OR (ep.exam_paper_code LIKE :#{"%" + #request.codeAndTeacher + "%"})
                     OR (CONCAT(s.staff_code,' - ',s.name) LIKE :#{"%" + #request.codeAndTeacher + "%"}))
            AND ((:#{#request.startDate} IS NULL) 
                     OR (:#{#request.endDate} IS NULL) 
                     OR ((ep.created_exam_paper_date BETWEEN :#{#request.startDate} AND :#{#request.endDate})))
            """,nativeQuery = true)
    List<TMockExamPaperResponse> getMockExamPapers(TMockExamPaperRequest request,String idFacility);

}
