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
            SELECT
            	s.id AS id,
            	ROW_NUMBER() OVER(
                ORDER BY hsbs.id DESC) AS orderNumber,
                (
                    SELECT ep.path
                    FROM exam_paper ep
                    WHERE
                        ep.exam_paper_type = "SAMPLE_EXAM_PAPER" AND
                        ep.id_subject = s.id
                ) as fileId,
            	s.subject_code AS subjectCode,
            	s.name AS subjectName,
            	s.subject_type AS subjectType,
            	d.name AS departmentName,
            	s.created_date AS createdDate
            FROM
                head_subject_by_semester hsbs
            JOIN subject_group sg ON
                sg.id = hsbs.id_subject_group
            JOIN subject_by_subject_group sbsg ON
                sbsg.id_subject_group = sg.id
            JOIN subject s ON
                s.id = sbsg.id_subject
            JOIN department d ON
                s.id_department = d.id
            WHERE
                hsbs.id_staff = :#{#request.staffId} AND
                hsbs.id_semester = :semesterId AND
                sg.id_department_facility = :departmentFacilityId AND
                sg.status = 0 AND
                (
                    (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', TRIM(:#{#request.subjectCode}), '%')) AND
                    (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', TRIM(:#{#request.subjectName}), '%'))
                )
            """,
            countQuery = """
            SELECT COUNT(hsbs.id)
            FROM
                head_subject_by_semester hsbs
            JOIN subject_group sg ON
                sg.id = hsbs.id_subject_group
            JOIN subject_by_subject_group sbsg ON
                sbsg.id_subject_group = sg.id
            JOIN subject s ON
                s.id = sbsg.id_subject
            JOIN department d ON
                s.id_department = d.id
            WHERE
                hsbs.id_staff = :#{#request.staffId} AND
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
