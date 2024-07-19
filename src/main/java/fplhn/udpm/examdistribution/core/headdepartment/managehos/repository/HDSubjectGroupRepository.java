package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.RoleSubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectBySubjectGroupRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.RoleSubjectGroupResponse;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SubjectBySubjectGroupResponse;
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
                        ROW_NUMBER() over (ORDER BY sg.id) as orderNumber,
                        sg.id as id,
                        sg.attach_role_name as attachRoleName,
                        CONCAT(se.name, ' - ', se.year) as semesterInfo
                    FROM subject_group sg
                    LEFT JOIN department_facility df ON sg.id_department_facility = df.id
                    LEFT JOIN semester se ON sg.id_semester = se.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND sg.id_semester = :#{#request.semesterId}
                        AND (:#{#request.q} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM subject_group sg
                    LEFT JOIN department_facility df ON sg.id_department_facility = df.id
                    LEFT JOIN semester se ON sg.id_semester = se.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND sg.id_semester = :#{#request.semesterId}
                        AND (:#{#request.q} IS NULL OR sg.attach_role_name LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            nativeQuery = true
    )
    Page<RoleSubjectGroupResponse> getRoleSubjectGroup(Pageable pageable, RoleSubjectGroupRequest request);

    Optional<SubjectGroup> findByAttachRoleName(String attachRoleName);

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() over (ORDER BY s.id) as orderNumber,
                        s.id as id,
                        s.subject_code as subjectCode,
                        s.name as subjectName,
                        s.subject_type as subjectType,
                        s.id_subject_group = :#{#request.subjectGroupId} as isSubjectInGroup
                    FROM subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                    """,
            nativeQuery = true
    )
    Page<SubjectBySubjectGroupResponse> getSubjectBySubjectGroup(Pageable pageable, SubjectBySubjectGroupRequest request);

}
