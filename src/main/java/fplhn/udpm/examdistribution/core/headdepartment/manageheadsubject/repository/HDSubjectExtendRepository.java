package fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.repository;

import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.request.SubjectsStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.StaffsBySubjectResponse;
import fplhn.udpm.examdistribution.core.headdepartment.manageheadsubject.model.response.SubjectsStaffResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HDSubjectExtendRepository extends SubjectRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY s.id) AS orderNumber,
                        s.id AS id,
                        s.subject_code AS subjectCode,
                        s.name AS subjectName,
                        s.subject_type AS subjectType,
                        CONCAT(st.staff_code, ' - ', st.name) AS staffInChargeInfo
                    FROM
                        subject s
                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_subject AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN staff st ON hsbs.id_staff = st.id
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.semesterId} IS NULL OR hsbs.id_semester = :#{#request.semesterId} OR hsbs.id_semester IS NULL)
                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
                        AND (:#{#request.staffCode} IS NULL OR st.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
                        AND (:#{#request.staffName} IS NULL OR st.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
                    """,
            countQuery = """
                    SELECT
                        COUNT(*)
                    FROM
                        subject s
                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_subject AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN staff st ON hsbs.id_staff = st.id
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.semesterId} IS NULL OR hsbs.id_semester = :#{#request.semesterId} OR hsbs.id_semester IS NULL)
                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
                        AND (:#{#request.staffCode} IS NULL OR st.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
                        AND (:#{#request.staffName} IS NULL OR st.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
                    """,
            nativeQuery = true
    )
    Page<SubjectsStaffResponse> getSubjectsStaff(Pageable pageable, SubjectsStaffRequest request);

    @Query(
            value = """
                    SELECT
                        s.id AS id,
                        s.staff_code AS staffCode,
                        s.name AS staffName,
                        s.account_fpt AS accountFPT,
                        s.account_fe AS accountFE,
                        MAX(CASE WHEN hsbs.id_subject = :#{#request.subjectId} THEN true ELSE false END) AS isHeadOfSubject
                    FROM
                        staff s
                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_staff AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN subject su ON hsbs.id_subject = su.id
                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
                    WHERE
                        s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%') OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                    GROUP BY
                        s.id, s.staff_code, s.name, s.account_fpt, s.account_fe
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT s.id)
                    FROM
                        staff s
                    LEFT JOIN head_subject_by_semester hsbs ON s.id = hsbs.id_staff AND hsbs.id_semester = :#{#request.semesterId}
                    LEFT JOIN subject su ON hsbs.id_subject = su.id
                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
                    WHERE
                        s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%') OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
                    """,
            nativeQuery = true
    )
    Page<StaffsBySubjectResponse> getStaffsBySubject(StaffsBySubjectRequest request, Pageable pageable);

}
