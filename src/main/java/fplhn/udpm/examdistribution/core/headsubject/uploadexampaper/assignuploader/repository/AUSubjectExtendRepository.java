package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.SubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AUSubjectExtendRepository extends SubjectRepository {

    @Query(value = """
            SELECT 	s.id AS id,
            		ROW_NUMBER() OVER(
                    ORDER BY s.id DESC) AS orderNumber,
                    s.subject_code AS subjectCode,
                    s.name AS subjectName,
                    s.subject_type AS subjectType,
                    d.name AS departmentName,
                    s.created_date AS createdDate
            FROM subject s
            JOIN department d ON s.id_department = d.id
            JOIN department_facility df ON df.id_department = d.id
            JOIN staff st ON st.id = s.id_head_subject
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
                    JOIN staff st ON st.id = s.id_head_subject
                    WHERE
                        (df.id = :departmentFacilityId) AND
                        (st.id = :#{#request.staffId}) AND
                        (s.status = 0) AND
                        (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE :#{"%" + #request.subjectCode + "%"}) AND
                        (:#{#request.subjectName} IS NULL OR s.name LIKE :#{"%" + #request.subjectName + "%"})
                    """, nativeQuery = true)
    Page<SubjectResponse> getAllSubject(Pageable pageable, String departmentFacilityId, FindSubjectRequest request);

}
