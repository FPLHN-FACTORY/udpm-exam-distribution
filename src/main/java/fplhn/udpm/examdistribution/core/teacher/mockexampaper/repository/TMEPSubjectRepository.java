package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TSubjectMockExamRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TSubjectMockExamResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TMEPSubjectRepository extends SubjectRepository {

    @Query(value = """
            SELECT CONCAT(s.subject_code, ' - ',s.name) AS subjectName,
                   d.name AS departmentName,
                   s.id AS id,
                   s.subject_type AS subjectType,
                   CONCAT(s2.name,' - ',s2.year) AS semesterName,
                   b2.name AS blockName,
                   b2.id as blockId,
                   CASE
                       WHEN b2.id = :currentBlockId THEN 'TRUE'
                       ELSE 'FALSE'
                   END AS isCurrentBlock,
                   CASE
                       WHEN EXISTS (SELECT 1
                            FROM practice_room pr
                            WHERE pr.id_facility = :facilityId
                            AND pr.id_block = b2.id
                            AND pr.id_staff = :staffId
                            AND pr.id_subject = s.id
                            AND pr.status = 0) THEN 'TRUE'
                       ELSE 'FALSE'
                   END AS isExistsPracticeRoom,
                   (SELECT pr.id
                        FROM practice_room pr
                        WHERE pr.id_facility = :facilityId
                        AND pr.id_block = b2.id
                        AND pr.id_staff = :staffId
                        AND pr.id_subject = s.id
                        AND pr.status = 0
                        LIMIT 1) AS practiceRoomId
            FROM class_subject cs
            JOIN facility_child fc ON fc.id = cs.id_facility_child
            JOIN subject s ON s.id = cs.id_subject
            JOIN block b2 ON b2.id = cs.id_block
            JOIN semester s2 ON s2.id = b2.id_semester
            JOIN department d ON d.id = s.id_department
            WHERE cs.id_staff LIKE :staffId
            AND fc.id_facility LIKE :facilityId
            AND (:#{#request.subjectAndDepartment} IS NULL
                     OR (d.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (CONCAT(s.subject_code, ' - ',s.name) LIKE :#{"%" + #request.subjectAndDepartment + "%"}))
            AND (:#{#request.semester} IS NULL OR s2.id LIKE :#{#request.semester})
            AND (:#{#request.block} IS NULL OR b2.name LIKE :#{#request.block})
            AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{"%" + #request.subjectType + "%"})
            GROUP BY CONCAT(s.subject_code, ' - ',s.name) ,
                     d.name,
                     s.id,
                     s.subject_type,
                     CONCAT(s2.name,' - ',s2.year),
                     b2.name,
                     CASE
                        WHEN b2.id = :currentBlockId THEN 'TRUE'
                        ELSE 'FALSE'
                     END,
                     CASE
                     WHEN EXISTS (SELECT 1
                                FROM practice_room pr
                                WHERE pr.id_facility = :facilityId
                                AND pr.id_block = b2.id
                                AND pr.id_staff = :staffId
                                AND pr.id_subject = s.id
                                AND pr.status = 0) THEN 'TRUE'
                     ELSE 'FALSE'
                     END,
                     (SELECT pr.id
                     FROM practice_room pr
                     WHERE pr.id_facility = :facilityId
                     AND pr.id_block = b2.id
                     AND pr.id_staff = :staffId
                     AND pr.id_subject = s.id
                     AND pr.status = 0
                     LIMIT 1),
                     cs.id
            """,
            countQuery = """
            SELECT 	COUNT(cs.id)
            FROM class_subject cs
            JOIN subject s ON s.id = cs.id_subject
            JOIN block b2 ON b2.id = cs.id_block
            JOIN semester s2 ON s2.id = b2.id_semester
            JOIN department d ON d.id = s.id_department
            WHERE cs.id_staff LIKE :staffId
            AND fc.id_facility LIKE :facilityId
            AND (:#{#request.subjectAndDepartment} IS NULL
                     OR (d.name LIKE :#{"%" + #request.subjectAndDepartment + "%"})
                     OR (CONCAT(s.subject_code, ' - ',s.name) LIKE :#{"%" + #request.subjectAndDepartment + "%"}))
            AND (:#{#request.semester} IS NULL OR s2.id LIKE :#{#request.semester})
            AND (:#{#request.block} IS NULL OR b2.name LIKE :#{#request.block})
            AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE :#{"%" + #request.subjectType + "%"})
            GROUP BY CONCAT(s.subject_code, ' - ',s.name) ,
                     d.name,
                     s.id,
                     s.subject_type,
                     CONCAT(s2.name,' - ',s2.year),
                     b2.name,
                     CASE
                        WHEN b2.id = :currentBlockId THEN 'TRUE'
                        ELSE 'FALSE'
                     END,
                     CASE
                     WHEN EXISTS (SELECT 1
                                FROM practice_room pr
                                WHERE pr.id_facility = :facilityId
                                AND pr.id_block = b2.id
                                AND pr.id_staff = :staffId
                                AND pr.id_subject = s.id
                                AND pr.status = 0) THEN 'TRUE'
                     ELSE 'FALSE'
                     END,
                     (SELECT pr.id
                        FROM practice_room pr
                        WHERE pr.id_facility = :facilityId
                        AND pr.id_block = b2.id
                        AND pr.id_staff = :staffId
                        AND pr.id_subject = s.id
                        AND pr.status = 0
                        LIMIT 1),
                     cs.id
            """, nativeQuery = true)
    Page<TSubjectMockExamResponse> getAllSubject(Pageable pageable, String staffId,String facilityId,String currentBlockId, TSubjectMockExamRequest request);


}
