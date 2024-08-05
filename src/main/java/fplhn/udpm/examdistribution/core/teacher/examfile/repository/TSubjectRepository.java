package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.core.teacher.examfile.model.request.TFindSubjectRequest;
import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TSubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface TSubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT s.id AS id,
                    ROW_NUMBER() OVER(
                    ORDER BY s.id DESC) AS orderNumber,
                    s.subject_code AS subjectCode,
                    s.name AS subjectName,
                    s.subject_type AS subjectType,
                    d.name AS departmentName,
                    s.created_date AS createdDate,
                    au.max_upload AS maxUpload,
                    (SELECT COUNT(ep.id)
                     FROM exam_paper ep
                     JOIN subject s3 ON s3.id = ep.id_subject
                     WHERE s3.id = s.id
                     AND ep.id_staff_upload = :staffId
                     AND (ep.exam_paper_type != 'SAMPLE_EXAM_PAPER' OR ep.exam_paper_type IS NULL)) AS uploaded
            FROM subject s
            JOIN department d ON s.id_department = d.id
            JOIN department_facility df ON df.id_department = d.id
            JOIN assign_uploader au ON au.id_subject = s.id
            JOIN semester s2 ON s2.id = au.id_semester
            JOIN block b ON b.id_semester = s2.id
            WHERE au.id_staff = :staffId
            AND (df.id = :departmentFacilityId)
            AND au.status = 0
            AND (s.status = 0)
            AND (SELECT UNIX_TIMESTAMP(NOW(3)) * 1000) BETWEEN b.start_time AND b.end_time
            AND (:#{#request.findSubject} IS NULL
                     OR s.subject_code LIKE :#{"%" + #request.findSubject + "%"}
                     OR s.name LIKE :#{"%" + #request.findSubject + "%"})
            AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{#request.subjectType})
            """,
            countQuery = """
                 SELECT COUNT(s.id)
                 FROM subject s
                 JOIN department d ON s.id_department = d.id
                 JOIN department_facility df ON df.id_department = d.id
                 JOIN assign_uploader au ON au.id_subject = s.id
                 JOIN semester s2 ON s2.id = au.id_semester
                 JOIN block b ON b.id_semester = s2.id
                 WHERE au.id_staff = :staffId
                 AND (df.id = :departmentFacilityId)
                 AND au.status = 0
                 AND (s.status = 0) 
                 AND (SELECT UNIX_TIMESTAMP(NOW(3)) * 1000) BETWEEN b.start_time AND b.end_time
                 AND (:#{#request.findSubject} IS NULL
                          OR s.subject_code LIKE :#{"%" + #request.findSubject + "%"}
                          OR s.name LIKE :#{"%" + #request.findSubject + "%"})
                 AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{#request.subjectType})
                 """, nativeQuery = true)
    Page<TSubjectResponse> getAllSubject(Pageable pageable, String departmentFacilityId, TFindSubjectRequest request,String staffId);

    @Query(value = """
            SELECT s.id AS id,
                    ROW_NUMBER() OVER(
                    ORDER BY s.id DESC) AS orderNumber,
                    s.id AS subjectId,
                    s.subject_code AS subjectCode,
                    s.name AS subjectName,
                    s.subject_type AS subjectType,
                    d.name AS departmentName,
                    s.created_date AS createdDate,
                    au.max_upload AS maxUpload,
                    (SELECT COUNT(ep.id)
                     FROM exam_paper ep
                     JOIN subject s3 ON s3.id = ep.id_subject
                     WHERE s3.id = s.id
                     AND ep.id_staff_upload = :staffId  
                     AND (ep.exam_paper_type != 'SAMPLE_EXAM_PAPER' OR ep.exam_paper_type IS NULL)) AS uploaded
            FROM subject s
            JOIN department d ON s.id_department = d.id
            JOIN department_facility df ON df.id_department = d.id
            JOIN assign_uploader au ON au.id_subject = s.id
            JOIN semester s2 ON s2.id = au.id_semester
            JOIN block b ON b.id_semester = s2.id
            WHERE au.id_staff LIKE :staffId
            AND (df.id = :departmentFacilityId)
            AND au.status = 0
            AND (s.status = 0)
            AND (SELECT UNIX_TIMESTAMP(NOW(3)) * 1000) BETWEEN b.start_time AND b.end_time
            AND s.id = :subjectId
            """,nativeQuery = true)
    Optional<TSubjectResponse> getSubjectById(String departmentFacilityId, String staffId, String subjectId);

}
