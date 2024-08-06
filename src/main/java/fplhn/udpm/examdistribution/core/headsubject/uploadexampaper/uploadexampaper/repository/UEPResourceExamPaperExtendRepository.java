package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListResourceExamPaperRequest;
import fplhn.udpm.examdistribution.repository.ResourceExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UEPResourceExamPaperExtendRepository extends ResourceExamPaperRepository {

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
    Page<Object> getListResourceExamPaper(Pageable pageable, ListResourceExamPaperRequest request);

}
