package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository;

import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.HeadSubjectResponse;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.repository.HeadSubjectBySemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface HDHeadSubjectBySemesterExtendRepository extends HeadSubjectBySemesterRepository {

    @Query(
            value = """
                    WITH current_head_subject AS (
                        SELECT
                            hs.id_staff AS staff_id,
                            sg.attach_role_name AS role_name,
                            CONCAT(sem.name, ' - ', sem.year) AS semester_info
                        FROM head_subject_by_semester hs
                        LEFT JOIN subject_group sg ON hs.id_subject_group = sg.id
                        LEFT JOIN semester sem ON hs.id_semester = sem.id
                        WHERE sg.id_department_facility = :#{#request.departmentFacilityId}
                            AND sg.id_semester = :#{#request.semesterId}
                    )
                    SELECT
                        ROW_NUMBER() over (ORDER BY s.staff_code) AS orderNumber,
                        s.id AS id,
                        s.staff_code AS staffCode,
                        s.name AS staffName,
                        s.account_fpt AS accountFPT,
                        s.account_fe AS accountFE,
                        CASE
                            WHEN s.id IN (SELECT staff_id FROM current_head_subject)
                            THEN (SELECT role_name FROM current_head_subject WHERE staff_id = s.id LIMIT 1)
                            ELSE 'Chưa phân công'
                        END AS roleName,
                        CASE
                            WHEN s.id IN (SELECT staff_id FROM current_head_subject)
                            THEN (SELECT semester_info FROM current_head_subject WHERE staff_id = s.id LIMIT 1)
                            ELSE 'Chưa phân công'
                        END AS semesterInfo
                    FROM staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON smf.id_major_facility = mf.id
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND s.id != :#{#request.currentUserId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            countQuery = """
                    SELECT COUNT(s.id)
                    FROM staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON smf.id_major_facility = mf.id
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND s.id != :#{#request.currentUserId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            nativeQuery = true
    )
    Page<HeadSubjectResponse> getHeadSubjects(Pageable pageable, HeadSubjectRequest request);

    Optional<HeadSubjectBySemester> findBySubjectGroup_IdAndSemester_Id(String subjectGroupId, String semesterId);

    Boolean existsBySubjectGroupIdAndStaffIdAndSemesterId(String subjectGroupId, String staffId, String semesterId);

    Boolean existsByStaffIdAndSemesterId(String staffId, String semesterId);

    Optional<HeadSubjectBySemester> findByStaff_IdAndSemester_Id(String staffId, String semesterId);

}
