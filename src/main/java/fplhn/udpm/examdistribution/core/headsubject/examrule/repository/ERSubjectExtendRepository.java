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
                    ORDER BY hsbs.id DESC
                ) AS orderNumber,
                s.subject_code AS subjectCode,
                s.name AS subjectName,
                s.subject_type AS subjectType,
                d.name AS departmentName,
                s.created_date AS createdDate,
                s.path_exam_rule AS fileId
            FROM
                head_subject_by_semester hsbs
            JOIN subject_group sg ON
                hsbs.id_subject_group = sg.id
            JOIN subject s ON
                s.id = sg.id_subject
            JOIN department d ON
                s.id_department = d.id
            WHERE
                sg.id_staff = :#{#request.staffId} AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0 AND
                (
                    (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', TRIM(:#{#request.subjectCode}), '%')) AND
                    (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', TRIM(:#{#request.subjectName}), '%'))
                )
            """,
            countQuery = """
                    SELECT 	COUNT(hsbs.id)
                    FROM
                        head_subject_by_semester hsbs
                    JOIN subject_group sg ON
                        hsbs.id_subject_group = sg.id
                    JOIN subject s ON
                        s.id = sg.id_subject
                    JOIN department d ON
                        s.id_department = d.id
                    WHERE
                        sg.id_staff = :#{#request.staffId} AND
                        hsbs.id_semester = :semesterId AND
                        sg.id_department_facility = :departmentFacilityId AND
                        sg.status = 0 AND
                        (
                            (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', TRIM(:#{#request.subjectCode}), '%')) AND
                            (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', TRIM(:#{#request.subjectName}), '%'))
                        )
                            """, nativeQuery = true)
    Page<SubjectResponse> getAllSubject(Pageable pageable, String departmentFacilityId, String semesterId, FindSubjectRequest request);

}
