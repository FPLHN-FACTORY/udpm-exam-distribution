package fplhn.udpm.examdistribution.core.headoffice.examrule.repository;

import fplhn.udpm.examdistribution.core.headoffice.examrule.model.request.HOFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.examrule.model.response.HOSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HOSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT
            	s.id AS id,
            	ROW_NUMBER() OVER(
                    ORDER BY s.id DESC
                ) AS orderNumber,
            	s.subject_code AS code,
            	s.name AS name,
            	(
            	    SELECT
            	        CASE
            	            WHEN COUNT(subj.id_exam_rule) > 0 THEN 1
            	            ELSE 0
            	        END
            	    FROM subject subj
            	    WHERE
            	        subj.id_exam_rule = :#{#request.examRuleId} AND
            	        subj.id = s.id AND
            	        subj.status = 0
            	) AS isChecked
            FROM
                head_subject_by_semester hsbs
            JOIN subject s ON
                s.id = hsbs.id_subject
            JOIN department d ON
                d.id = s.id_department
            JOIN department_facility df ON
                df.id_department = d.id
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                df.id = :departmentFacilityId AND
                hsbs.status = 0 AND
                (
                    :#{#request.valueSearch} IS NULL OR s.name LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%') OR
                    :#{#request.valueSearch} IS NULL OR s.subject_code LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%')
                )
            """, countQuery = """
            SELECT
            	COUNT(hsbs.id)
            FROM
                head_subject_by_semester hsbs
            JOIN subject s ON
                s.id = hsbs.id_subject
            JOIN department d ON
                d.id = s.id_department
            JOIN department_facility df ON
                df.id_department = d.id
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                df.id = :departmentFacilityId AND
                hsbs.status = 0 AND
                (
                    :#{#request.valueSearch} IS NULL OR s.name LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%') OR
                    :#{#request.valueSearch} IS NULL OR s.subject_code LIKE CONCAT('%',TRIM(:#{#request.valueSearch}),'%')
                )
            """, nativeQuery = true)
    Page<HOSubjectResponse> getListSubject(
            Pageable pageable, String userId, String departmentFacilityId, String semesterId,
            HOFindSubjectRequest request
    );

}
