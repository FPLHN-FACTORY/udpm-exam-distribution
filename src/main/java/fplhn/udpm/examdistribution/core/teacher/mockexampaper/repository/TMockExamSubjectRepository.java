package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TSubjectMockExamRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TSubjectMockExamResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TMockExamSubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT CONCAT(s.subject_code, ' - ',s.name) AS subjectName,
            	   d.name AS departmentName,
            	   s.id AS id,
            	   s.subject_type AS subjectType,
            	   s.subject_status AS subjectStatus,
            	   CONCAT(s2.name,' - ',s2.year) AS semesterName
            FROM staff_subject ss
            JOIN subject s ON s.id = ss.id_subject
            JOIN semester s2 ON s2.id = ss.id_recently_semester
            JOIN department d ON d.id = s.id_department
            JOIN block b ON b.id_semester = s2.id
            WHERE ss.id_staff LIKE :staffId
            AND (SELECT UNIX_TIMESTAMP(NOW(3)) * 1000) BETWEEN b.start_time AND b.end_time
            AND (:#{#request.subjectAndDepartment} IS NULL
                     OR (d.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (s.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (s.subject_code LIKE :#{"%" + #request.subjectAndDepartment + "%"}))
            AND (:#{#request.semester} IS NULL OR s2.id LIKE :#{#request.semester})
            AND (:#{#request.subjectStatus} IS NULL OR s.subject_status LIKE :#{"%" + #request.subjectStatus + "%"})
            AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{"%" + #request.subjectType + "%"})
            """,
            countQuery = """
            SELECT 	COUNT(ss.id)
            FROM staff_subject ss
            JOIN subject s ON s.id = ss.id_subject
            JOIN semester s2 ON s2.id = ss.id_recently_semester
            JOIN department d ON d.id = s.id_department
            JOIN block b ON b.id_semester = s2.id
            WHERE ss.id_staff LIKE :staffId
            AND (SELECT UNIX_TIMESTAMP(NOW(3)) * 1000) BETWEEN b.start_time AND b.end_time
            AND (:#{#request.subjectAndDepartment} IS NULL
                     OR (d.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (s.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (s.subject_code LIKE :#{"%" + #request.subjectAndDepartment + "%"}))
            AND (:#{#request.semester} IS NULL OR s2.id LIKE :#{#request.semester})
            AND (:#{#request.subjectStatus} IS NULL OR s.subject_status LIKE :#{"%" + #request.subjectStatus + "%"})
            AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{"%" + #request.subjectType + "%"})
            """, nativeQuery = true)
    Page<TSubjectMockExamResponse> getAllSubject(Pageable pageable, String staffId, TSubjectMockExamRequest request);


}
