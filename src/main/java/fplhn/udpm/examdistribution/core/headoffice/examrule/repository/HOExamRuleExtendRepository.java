package fplhn.udpm.examdistribution.core.headoffice.examrule.repository;

import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindExamRuleRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.response.HOExamRuleResponse;
import fplhn.udpm.examdistribution.repository.ExamRuleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HOExamRuleExtendRepository extends ExamRuleRepository {

    @Query(value = """
            SELECT
                er.id AS id,
                ROW_NUMBER() OVER(
                    ORDER BY er.id DESC
                ) AS orderNumber,
                er.name AS name,
                er.file_id AS fileId,
                er.status AS status
            FROM exam_rule er
            WHERE
                :#{#request.valueSearch} IS NULL OR er.name LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%') AND
                er.status = 0
            """,
            countQuery = """
                    SELECT 	COUNT(er.id)
                    FROM exam_rule er
                    WHERE
                        :#{#request.valueSearch} IS NULL OR er.name LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%') AND
                        er.status = 0
            """, nativeQuery = true)
    Page<HOExamRuleResponse> getAllExamRule(Pageable pageable, HOFindExamRuleRequest request);

}
