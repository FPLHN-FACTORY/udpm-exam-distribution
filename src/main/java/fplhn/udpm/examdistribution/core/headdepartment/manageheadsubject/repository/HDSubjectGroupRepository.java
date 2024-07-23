package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository;

import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectBySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectGroupAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.RoleSubjectGroupResponse;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.SubjectBySubjectGroupResponse;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.SubjectGroupAssignedResponse;
import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.repository.SubjectGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDSubjectGroupRepository extends SubjectGroupRepository {

    @Query(
            value = """
                    SELECT
                        sg.id AS id,
                        ROW_NUMBER() over (ORDER BY sg.created_date DESC) AS orderNumber,
                        sg.attach_role_name AS attachRoleName,
                        CONCAT(s.name, ' - ', s.year) AS semesterInfo
                    FROM subject_group sg
                    LEFT JOIN semester s ON sg.id_semester = s.id
                    WHERE
                        s.id = :#{#request.semesterId}
                        AND sg.id_department_facility = :#{#request.departmentFacilityId}
                        AND :#{#request.attachRoleName} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.attachRoleName}, '%')
                    """,
            countQuery = """
                    SELECT
                        COUNT(sg.id)
                    FROM subject_group sg
                    LEFT JOIN semester s ON sg.id_semester = s.id
                    WHERE
                        s.id = :#{#request.semesterId}
                        AND sg.id_department_facility = :#{#request.departmentFacilityId}
                        AND :#{#request.attachRoleName} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.attachRoleName}, '%')
                    """,
            nativeQuery = true
    )
    Page<RoleSubjectGroupResponse> getRoleSubjectGroup(Pageable pageable, RoleSubjectGroupRequest request);


    Optional<SubjectGroup> findByAttachRoleName(String attachRoleName);

    Boolean existsByAttachRoleNameAndSemesterIdAndDepartmentFacilityId(
            String attachRoleName,
            String semesterId,
            String departmentFacilityId
    );

    @Query(
            value = """
                    WITH current_id_subject_in_group AS (
                        SELECT s.id AS id
                        FROM subject s
                        LEFT JOIN subject_by_subject_group sbg ON s.id = sbg.id_subject
                        LEFT JOIN subject_group sg ON sbg.id_subject_group = sg.id
                        WHERE
                            sg.id = :#{#request.subjectGroupId}
                            AND sg.id_department_facility = :#{#request.departmentFacilityId}
                            AND sg.id_semester = :#{#request.semesterId}
                    )
                    SELECT
                        s.id AS id,
                        ROW_NUMBER() OVER (ORDER BY s.created_date DESC) AS orderNumber,
                        s.name AS subjectName,
                        s.subject_code AS subjectCode,
                        s.subject_type AS subjectType,
                        CASE WHEN s.id IN (SELECT id FROM current_id_subject_in_group) THEN TRUE ELSE FALSE END AS isSubjectInGroup
                    FROM subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND s.name IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%')
                        AND s.subject_code IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.q}, '%')
                    """,
            countQuery = """
                    SELECT COUNT(s.id)
                    FROM subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND s.name IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%')
                        AND s.subject_code IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.q}, '%')
                    """,
            nativeQuery = true
    )
    Page<SubjectBySubjectGroupResponse> findAllSubjectsInSubjectGroup(
            Pageable pageable,
            SubjectBySubjectGroupRequest request
    );

    @Query(
            value = """
                    SELECT
                        sg.id AS id,
                        ROW_NUMBER() over (ORDER BY sg.created_date DESC) AS orderNumber,
                        sg.attach_role_name AS attachRoleName,
                        CASE
                            WHEN sg.id IN (
                                SELECT id_subject_group
                                FROM head_subject_by_semester
                                WHERE id_staff = :#{#request.staffId}
                            )
                            THEN TRUE
                            ELSE FALSE
                        END AS assigned
                    FROM subject_group sg
                    LEFT JOIN head_subject_by_semester hs ON sg.id = hs.id_subject_group
                    WHERE
                        sg.id_department_facility = :#{#request.departmentFacilityId}
                        AND sg.id_semester = :#{#request.semesterId}
                        AND :#{#request.attachRoleName} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.attachRoleName}, '%')
                    """,
            countQuery = """
                    SELECT
                        COUNT(sg.id)
                    FROM subject_group sg
                    LEFT JOIN head_subject_by_semester hs ON sg.id = hs.id_subject_group
                    WHERE
                        sg.id_department_facility = :#{#request.departmentFacilityId}
                        AND sg.id_semester = :#{#request.semesterId}
                        AND :#{#request.attachRoleName} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.attachRoleName}, '%')
                    """,
            nativeQuery = true
    )
    Page<SubjectGroupAssignedResponse> getSubjectGroups(Pageable pageable, SubjectGroupAssignedRequest request);

}
