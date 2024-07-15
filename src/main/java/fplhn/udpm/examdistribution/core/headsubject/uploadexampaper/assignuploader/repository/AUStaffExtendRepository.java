package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request.FindStaffRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.response.StaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AUStaffExtendRepository extends StaffRepository {

    @Query(value = """
            SELECT  s.id AS id,
                    ROW_NUMBER() OVER(
                    ORDER BY s.id DESC) AS orderNumber,
                    s.name AS name,
                    s.staff_code AS staffCode,
                    s.account_fe AS accountFE,
                    s.account_fpt AS accountFpt,
                    s.created_date AS createdDate,
                    (
                        SELECT
                            CASE
                                WHEN COUNT(ep.id) > 0 THEN 1
                                ELSE 0
                            END AS isHasSampleExamPaper
                        FROM exam_paper AS ep
                        JOIN block b ON b.id = ep.id_block
                        WHERE ep.id_subject = :#{#request.subjectId} AND
                              b.id_semester = :semesterId AND
                              ep.exam_paper_type = "SAMPLE_EXAM_PAPER"
                    ) AS isHasSampleExamPaper,
                    (
                        SELECT au.max_upload AS maxUpload
                        FROM assign_uploader au
                        WHERE au.id_staff = s.id AND
                              au.id_subject = :#{#request.subjectId} AND
                              au.id_semester = :semesterId
                    ) AS maxUpload,
                    (
                    	SELECT
                    		CASE
                    			WHEN COUNT(*) > 0 THEN 1
                    			ELSE 0
                    		END AS isAssigned
                    	FROM
                    		assign_uploader au
                    	JOIN staff st ON
                    		au.id_staff = st.id
                    	JOIN subject subj ON
                    		au.id_subject = subj.id
                    	WHERE
                    		subj.id = :#{#request.subjectId} AND
                    		st.id = s.id
                    ) AS isAssigned
            FROM staff s
            LEFT JOIN department_facility df ON df.id = s.id_department_facility
            LEFT JOIN department d ON d.id = df.id_department
            LEFT JOIN facility f ON f.id = df.id_facility
            WHERE (s.status = 0) AND
                  (s.id_department_facility = :departmentFacilityId) AND
                  (s.id != :userId)
            AND (
                 (:#{#request.staffName} IS NULL OR s.name LIKE :#{"%" + #request.staffName + "%"}) AND
                 (:#{#request.staffCode} IS NULL OR s.staff_code LIKE :#{"%" + #request.staffCode + "%"})
                )
            AND (
                 (:#{#request.accountFptOrFe} IS NULL OR s.account_fe LIKE :#{"%" + #request.accountFptOrFe + "%"}) OR
                 (:#{#request.accountFptOrFe} IS NULL OR s.account_fpt LIKE :#{"%" + #request.accountFptOrFe + "%"})
                )
            """,
            countQuery = """
                    SELECT COUNT(s.id)
                    FROM staff s
                    LEFT JOIN department_facility df ON df.id = s.id_department_facility
                    LEFT JOIN department d ON d.id = df.id_department
                    LEFT JOIN facility f ON f.id = df.id_facility
                    WHERE (s.status = 0) AND
                          (s.id_department_facility = :departmentFacilityId) AND
                          (s.id != :userId)
                    AND (
                         (:#{#request.staffName} IS NULL OR s.name LIKE :#{"%" + #request.staffName + "%"}) AND
                         (:#{#request.staffCode} IS NULL OR s.staff_code LIKE :#{"%" + #request.staffCode + "%"})
                        )
                    AND (
                         (:#{#request.accountFptOrFe} IS NULL OR s.account_fe LIKE :#{"%" + #request.accountFptOrFe + "%"}) OR
                         (:#{#request.accountFptOrFe} IS NULL OR s.account_fpt LIKE :#{"%" + #request.accountFptOrFe + "%"})
                        )
                    """,
            nativeQuery = true)
    Page<StaffResponse> getAllStaff(Pageable pageable, String departmentFacilityId, FindStaffRequest request, String userId, String semesterId);

}
