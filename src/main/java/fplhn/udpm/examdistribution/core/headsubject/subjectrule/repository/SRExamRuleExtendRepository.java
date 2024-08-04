package fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository;

import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRuleRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.response.SRExamRuleResponse;
import fplhn.udpm.examdistribution.repository.ExamRuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SRExamRuleExtendRepository extends ExamRuleRepository {

    @Query(value = """
            SELECT
                er.id AS id,
                ROW_NUMBER() OVER(
                    ORDER BY er.id DESC
                ) AS orderNumber,
                er.name AS name,
                (
                    SELECT
                        CASE
                            WHEN subj.id_exam_rule = er.id THEN 1
                            ELSE 0
                        END
                    FROM subject subj
                    WHERE
                        subj.id_exam_rule = er.id AND
                        subj.id = :#{#request.subjectId} AND
                        subj.status = 0
                ) AS isChecked
            FROM exam_rule er
            WHERE
                (:#{#request.name} IS NULL OR er.name LIKE CONCAT('%',TRIM(:#{#request.name}),'%')) AND
                er.status = 0
            """,countQuery = """
            SELECT
                COUNT(er.id)
            FROM exam_rule er
            WHERE
                (:#{#request.name} IS NULL OR er.name LIKE CONCAT('%',TRIM(:#{#request.name}),'%')) AND
                er.status = 0
            """, nativeQuery = true)
    Page<SRExamRuleResponse> getListExamRule(Pageable pageable, SRFindSubjectRuleRequest request);

}
