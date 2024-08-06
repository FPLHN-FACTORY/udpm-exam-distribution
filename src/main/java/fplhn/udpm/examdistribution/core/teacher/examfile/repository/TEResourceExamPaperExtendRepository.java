package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TEListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TEListResourceExamPaperResponse;
import fplhn.udpm.examdistribution.entity.ResourceExamPaper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TEResourceExamPaperExtendRepository extends JpaRepository<ResourceExamPaper, String> {

    @Query(value = """
            SELECT
                erp.id AS id,
             	ROW_NUMBER() OVER(ORDER BY erp.id DESC) AS orderNumber,
                erp.resource AS resource
            FROM resource_exam_paper erp
            WHERE
                erp.id_exam_paper = :#{#request.examPaperId} AND
                erp.status = 0
            """, countQuery = """
            SELECT
                COUNT(erp.id)
            FROM resource_exam_paper erp
            WHERE
                erp.id_exam_paper = :#{#request.examPaperId} AND
                erp.status = 0
            """, nativeQuery = true)
    Page<TEListResourceExamPaperResponse> getListResourceExamPaper(Pageable pageable, TEListResourceExamPaperRequest request);

}
