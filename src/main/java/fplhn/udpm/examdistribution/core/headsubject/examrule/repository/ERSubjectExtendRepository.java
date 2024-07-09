package fplhn.udpm.examdistribution.core.headsubject.examrule.repository;

import fplhn.udpm.examdistribution.core.headsubject.examrule.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.examrule.model.response.SubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ERSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT
            	s.id AS id,
            	ROW_NUMBER() OVER(
            ORDER BY
            	s.id DESC) AS orderNumber,
            	s.subject_code AS subjectCode,
            	s.name AS subjectName,
            	s.subject_type AS subjectType,
            	d.name AS departmentName,
            	s.created_date AS createdDate,
            	s.path_exam_rule AS fileId
            FROM
            	subject s
            JOIN department d ON
            	s.id_department = d.id
            JOIN department_facility df ON
            	df.id_department = d.id
            JOIN head_subject_by_semester hsbs ON
            	hsbs.id_subject = s.id
            JOIN staff st ON
            	st.id = hsbs.id_staff
            WHERE
                (df.id = :departmentFacilityId) AND
                (st.id = :#{#request.staffId}) AND
                (s.status = 0) AND
                (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE :#{"%" + #request.subjectCode + "%"}) AND
                (:#{#request.subjectName} IS NULL OR s.name LIKE :#{"%" + #request.subjectName + "%"})
            """,
            countQuery = """
                    SELECT 	COUNT(s.id)
                    FROM subject s
                    JOIN department d ON s.id_department = d.id
                    JOIN department_facility df ON df.id_department = d.id
                    JOIN head_subject_by_semester hsbs ON hsbs.id_subject = s.id
                    JOIN staff st ON st.id = hsbs.id_staff
                    WHERE
                        (df.id = :departmentFacilityId) AND
                        (st.id = :#{#request.staffId}) AND
                        (s.status = 0) AND
                        (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE :#{"%" + #request.subjectCode + "%"}) AND
                        (:#{#request.subjectName} IS NULL OR s.name LIKE :#{"%" + #request.subjectName + "%"})
                    """, nativeQuery = true)
    Page<SubjectResponse> getAllSubject(Pageable pageable, String departmentFacilityId, FindSubjectRequest request);

}
