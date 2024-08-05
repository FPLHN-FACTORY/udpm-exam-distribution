//package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository;
//
//import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response.CEPListResourceExamPaperResponse;
//import fplhn.udpm.examdistribution.entity.ResourceExamPaper;
//import org.springframework.data.domain.Page;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;
//
//@Repository
//public interface CEPResourceExamPaperExtendRepository extends JpaRepository<ResourceExamPaper, String> {
//
//    @Query(value = """
//            SELECT
//                erp.id AS id,
//             	ROW_NUMBER() OVER(ORDER BY erp.id DESC) AS orderNumber,
//            FROM resource_exam_paper erp
//            WHERE
//                erp.id_exam_paper = :examPaperId AND
//                erp.status = 0
//            """,nativeQuery = true)
//    Page<CEPListResourceExamPaperResponse> getListResourceExamPaper(String examPaperId);
//
//}
