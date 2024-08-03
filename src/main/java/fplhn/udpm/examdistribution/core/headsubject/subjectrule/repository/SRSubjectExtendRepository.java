package fplhn.udpm.examdistribution.core.headsubject.subjectrule.repository;

import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.request.SRFindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.response.SRSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SRSubjectExtendRepository extends SubjectRepository {

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
                er.file_id AS fileId
            FROM
                head_subject_by_semester hsbs
            JOIN subject s ON
                s.id = hsbs.id_subject
            JOIN department d ON
                d.id = s.id_department
            JOIN department_facility df ON
                df.id_department = d.id
            JOIN exam_rule er ON
                er.id = s.id_exam_rule
            WHERE
                hsbs.id_staff = :userId AND
                hsbs.id_semester = :semesterId AND
                df.id = :departmentFacilityId AND
                hsbs.status = 0 AND
                s.status = 0 AND
                (
                    (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%',TRIM(:#{#request.subjectName}),'%')) AND
                    (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%',TRIM(:#{#request.subjectCode}),'%'))
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
                s.status = 0 AND
                (
                    (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%',TRIM(:#{#request.subjectName}),'%')) AND
                    (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%',TRIM(:#{#request.subjectCode}),'%'))
                )
            """, nativeQuery = true)
    Page<SRSubjectResponse> getListSubject(
            Pageable pageable, String userId, String departmentFacilityId, String semesterId,
            SRFindSubjectRequest request
    );

}
