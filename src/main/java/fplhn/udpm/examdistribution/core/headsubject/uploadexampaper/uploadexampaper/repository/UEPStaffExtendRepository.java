package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListStaffBySubjectIdRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListStaffBySubjectIdResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListStaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPStaffExtendRepository extends StaffRepository {

    @Query(value = """
            SELECT
            	st.id AS id,
            	CONCAT(st.name, "-", st.staff_code) AS name
            FROM staff_major_facility smf
            JOIN major_facility mf ON mf.id = smf.id_major_facility
            JOIN staff st ON st.id = smf.id_staff
            WHERE
                mf.id_department_facility = :departmentFacilityId AND
                smf.status = 0
            """, countQuery = """
            SELECT
            	COUNT(sdf.id)
            FROM staff_major_facility smf
            JOIN major_facility mf ON mf.id = smf.id_major_facility
            JOIN staff st ON st.id = smf.id_staff
            WHERE
                mf.id_department_facility = :departmentFacilityId AND
                smf.status = 0
            """, nativeQuery = true)
    List<ListStaffResponse> getListStaff(String departmentFacilityId);

    @Query(value = """
            SELECT
                s.id AS id,
             	ROW_NUMBER() OVER(ORDER BY s.id DESC) AS orderNumber,
                s.name AS name,
                s.staff_code AS code,
                s.account_fpt AS emailFpt,
                s.account_fe AS emailFe,
                (
                    SELECT
                        CASE
                            WHEN COUNT(spep.id) > 0 THEN 1
                            ELSE 0
                        END
                    FROM share_permission_exam_paper spep
                    JOIN exam_paper ep ON ep.id = spep.id_exam_paper
                    JOIN class_subject cs2 ON cs2.id_subject = ep.id_subject
                    WHERE spep.id_staff = s.id AND
                          cs.id = cs2.id AND
                          spep.id_block = :blockId AND
                          ep.id = :#{#request.examPaperId} AND
                          spep.id_facility = :facilityId AND
                          spep.status =0
                ) AS isPublic
            FROM class_subject cs
            JOIN staff s ON cs.id_staff = s.id
            JOIN facility_child fc ON fc.id = cs.id_facility_child
            WHERE
                cs.id_subject = :subjectId AND
                cs.id_block = :blockId AND
                fc.id_facility = :facilityId AND
                (
                    (:#{#request.searchValue} IS NULL OR s.name LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) OR
                    (:#{#request.searchValue} IS NULL OR s.staff_code LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) OR
                    (:#{#request.searchValue} IS NULL OR s.account_fpt LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) OR
                    (:#{#request.searchValue} IS NULL OR s.account_fe LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%'))
                ) AND
                s.status = 0
            """, countQuery = """
            SELECT
                COUNT(s.id)
            FROM class_subject cs
            JOIN staff s ON cs.id_staff = s.id
            JOIN facility_child fc ON fc.id = cs.id_facility_child
            WHERE
                cs.id_subject = :subjectId AND
                cs.id_block = :blockId AND
                fc.id_facility = :facilityId AND
                (
                    (:#{#request.searchValue} IS NULL OR s.name LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) AND
                    (:#{#request.searchValue} IS NULL OR s.staff_code LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) AND
                    (:#{#request.searchValue} IS NULL OR s.account_fpt LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%')) AND
                    (:#{#request.searchValue} IS NULL OR s.account_fe LIKE CONCAT('%', TRIM(:#{#request.searchValue}), '%'))
                ) AND
                s.status = 0
            """, nativeQuery = true)
    Page<ListStaffBySubjectIdResponse> getListStaffBySubjectId(Pageable pageable, String subjectId, String blockId, String facilityId, ListStaffBySubjectIdRequest request);

}
