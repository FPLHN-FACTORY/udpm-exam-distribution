package fplhn.udpm.examdistribution.core.headsubject.assignuploader.repository;

import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.request.FindSubjectRequest;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.response.StaffResponse;
import fplhn.udpm.examdistribution.core.headsubject.assignuploader.model.response.SubjectResponse;
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
            LEFT JOIN department d ON s.id_department = d.id
            LEFT JOIN department_facility df ON df.id_department = d.id
            WHERE
                (df.id = :departmentFacilityId) AND
                (s.status = 0) AND
                (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE :#{"%" + #request.subjectCode + "%"}) AND
                (:#{#request.subjectName} IS NULL OR s.name LIKE :#{"%" + #request.subjectName + "%"})
            """, nativeQuery = true)
    Page<SubjectResponse> getAllSubject(Pageable pageable, String departmentFacilityId, FindSubjectRequest request);

    @Query(value = """
            SELECT  s.id AS id,
                    ROW_NUMBER() OVER(
                    ORDER BY s.id DESC) AS orderNumber,
                    s.name AS name,
                    s.staff_code AS staffCode,
                    s.account_fe AS accountFE,
                    s.account_fpt AS accountFpt,
                    s.created_date AS createdDate,
                    CASE
                        WHEN au.id IS NOT NULL THEN 1
                        ELSE 0
                    END AS isAssigned
            FROM staff s
            LEFT JOIN department_facility df on df.id = s.id_department_facility
            LEFT JOIN department d ON d.id = df.id_department
            LEFT JOIN facility f ON f.id = df.id_facility
            LEFT JOIN assign_uploader au ON au.id_staff = s.id
            LEFT JOIN subject subj ON au.id_subject = subj.id
            WHERE s.status = 0 AND
                  s.id_department_facility = :departmentFacilityId
            AND (
                 (:#{#request.staffName} IS NULL OR s.name LIKE :#{"%" + #request.staffName + "%"}) AND
                 (:#{#request.staffCode} IS NULL OR s.staff_code LIKE :#{"%" + #request.staffCode + "%"})
                )
            AND (
                 (:#{#request.accountFptOrFe} IS NULL OR s.account_fe LIKE :#{"%" + #request.accountFptOrFe + "%"}) OR
                 (:#{#request.accountFptOrFe} IS NULL OR s.account_fpt LIKE :#{"%" + #request.accountFptOrFe + "%"})
                )
            """, nativeQuery = true)
    Page<StaffResponse> getAllStaff(Pageable pageable, String departmentFacilityId, FindStaffRequest request);

}
