package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.HeadSubjectResponse;
import fplhn.udpm.examdistribution.repository.HeadSubjectBySemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface HDHeadSubjectBySemesterExtendRepository extends HeadSubjectBySemesterRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY s.id DESC) as orderNumber,
                        s.id as id,
                        s.staff_code as staffCode,
                        s.name as staffName,
                        s.account_fe as accountFE,
                        s.account_fpt as accountFpt,
                        sg.attach_role_name as roleName,
                        MAX(CONCAT(se.name, ' - ', se.year)) as semesterInfo
                    FROM
                        staff s
                    LEFT JOIN staff_department_facility sdf ON s.id = sdf.id_staff
                    LEFT JOIN department_facility df ON sdf.id_department_facility = df.id
                    LEFT JOIN subject_group sg ON s.id = sg.id_staff
                    LEFT JOIN head_subject_by_semester hsbs ON  s.id = hsbs.id_staff AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN semester se ON hsbs.id_semester = se.id
                    LEFT JOIN staff_role sr ON s.id = sr.id_staff
                    LEFT JOIN role r ON sr.id_role = r.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId} AND s.id != :#{#request.currentUserId}
                        AND r.code = 'TRUONG_MON'
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
                    GROUP BY s.id, s.staff_code, s.name, s.account_fe, s.account_fpt, sg.attach_role_name
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT s.id)
                    FROM
                        staff s
                    LEFT JOIN staff_department_facility sdf ON s.id = sdf.id_staff
                    LEFT JOIN department_facility df ON sdf.id_department_facility = df.id
                    LEFT JOIN subject_group sg ON s.id = sg.id_staff
                    LEFT JOIN head_subject_by_semester hsbs ON  s.id = hsbs.id_staff AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN semester se ON hsbs.id_semester = se.id
                    LEFT JOIN staff_role sr ON s.id = sr.id_staff
                    LEFT JOIN role r ON sr.id_role = r.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId} AND s.id != :#{#request.currentUserId}
                        AND r.code = 'TRUONG_MON'
                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            nativeQuery = true
    )
    Page<HeadSubjectResponse> getHeadSubjects(Pageable pageable, HeadSubjectRequest request);
//
//
//    @Query(
//            value = """
//                    SELECT
//                        ROW_NUMBER() OVER (ORDER BY s.id) AS orderNumber,
//                        s.id AS id,
//                        s.subject_code AS subjectCode,
//                        s.name AS subjectName,
//                        s.subject_type AS subjectType,
//                        IF(hsbs.id_staff = :#{#request.staffId}, 1, 0) AS assigned
//                    FROM
//                        subject s
//                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_subject
//                    LEFT JOIN staff st ON hsbs.id_staff = st.id
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.q} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.subjectType} IS NULL OR s.subject_type = :#{#request.subjectType})
//                    """,
//            countQuery = """
//                    SELECT COUNT(*)
//                    FROM subject s
//                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_subject
//                    LEFT JOIN staff st ON hsbs.id_staff = st.id
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.q} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.subjectType} IS NULL OR s.subject_type = :#{#request.subjectType})
//                    """,
//            nativeQuery = true
//    )
//    Page<SubjectAssignedResponse> getSubjectAssigned(Pageable pageable, SubjectAssignedRequest request);

//    Optional<HeadSubjectBySemester> findBySubject_IdAndSemester_Id(String subjectId, String semesterId);

//    Optional<HeadSubjectBySemester> findBySubject_IdAndSemester_IdAndStaff_Id(String subjectId, String semesterId, String staffId);

}
