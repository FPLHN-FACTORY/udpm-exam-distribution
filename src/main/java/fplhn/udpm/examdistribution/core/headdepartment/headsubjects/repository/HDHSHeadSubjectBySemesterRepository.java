package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository;

import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.HeadSubjectSearchRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response.HeadSubjectResponse;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response.HeadSubjectSearchResponse;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.repository.HeadSubjectBySemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HDHSHeadSubjectBySemesterRepository extends HeadSubjectBySemesterRepository {

    @Query(
            value = """
                    SELECT 
                        ROW_NUMBER() OVER (ORDER BY s.id DESC ) as orderNumber,
                        s.id as id,
                        s.staff_code as staffCode,
                        s.name as staffName,
                        s.account_fpt as emailFPT,
                        s.account_fe as emailFE,
                        IF(MAX(hsbs.id) IS NOT NULL, TRUE, FALSE) as isAssigned,
                        COUNT(DISTINCT hsbs.id) as assignedCount
                    FROM
                        staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON mf.id = smf.id_major_facility
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_staff = s.id AND hsbs.id_semester = :#{#request.currentSemesterId}
                    LEFT JOIN semester se ON se.id = hsbs.id_semester
                    LEFT JOIN staff_role sr ON sr.id_staff = s.id
                    LEFT JOIN role r ON r.id = sr.id_role
                    WHERE
                        r.code = :#{#request.headSubjectRoleCode}
                        AND s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.currentDepartmentFacilityId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%',:#{#request.q},'%'))
                    GROUP BY s.id, s.staff_code, s.name, s.account_fpt, s.account_fe
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT s.id)
                    FROM
                        staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON mf.id = smf.id_major_facility
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_staff = s.id AND hsbs.id_semester = :#{#request.currentSemesterId}
                    LEFT JOIN semester se ON se.id = hsbs.id_semester
                    LEFT JOIN staff_role sr ON sr.id_staff = s.id
                    LEFT JOIN role r ON r.id = sr.id_role
                    WHERE
                        r.code = :#{#request.headSubjectRoleCode}
                        AND s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.currentDepartmentFacilityId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%',:#{#request.q},'%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%',:#{#request.q},'%'))
                    """,
            nativeQuery = true
    )
    Page<HeadSubjectResponse> getAllHeadSubjectsBySemester(Pageable pageable, HeadSubjectRequest request);

    Optional<HeadSubjectBySemester> findBySemester_IdAndSubject_IdAndFacility_Id(
            String semesterId,
            String subjectId,
            String facilityId
    );

    List<HeadSubjectBySemester> findBySemester_IdAndFacility_IdAndStaff_Id(
            String semesterId,
            String facilityId,
            String staffId
    );

    @Query(
            value = """
                    SELECT
                        s.id as id,
                        CONCAT(s.staff_code, ' - ', s.name) as staffInfo
                    FROM
                        staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON mf.id = smf.id_major_facility
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_staff = s.id AND hsbs.id_semester = :#{#request.currentSemesterId}
                    LEFT JOIN semester se ON se.id = hsbs.id_semester
                    LEFT JOIN staff_role sr ON sr.id_staff = s.id
                    LEFT JOIN role r ON r.id = sr.id_role
                    WHERE
                        r.code = :currentRoleName
                        AND r.id_facility = :#{#request.currentFacilityId}
                        AND s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.currentDepartmentFacilityId}
                        AND (:#{#request.q} IS NULL OR (s.staff_code LIKE CONCAT('%',:#{#request.q},'%') OR s.name LIKE CONCAT('%',:#{#request.q},'%')))
                    GROUP BY s.id, s.staff_code, s.name, s.account_fpt, s.account_fe
                    """,
            nativeQuery = true
    )
    List<HeadSubjectSearchResponse> getHeadSubjects(
            HeadSubjectSearchRequest request,
            String currentRoleName
    );

    List<HeadSubjectBySemester> findBySemester_Id(String semesterId);

}
