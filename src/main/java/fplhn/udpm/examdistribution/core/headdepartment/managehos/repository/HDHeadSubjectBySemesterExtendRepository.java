package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.HeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.HeadSubjectResponse;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SubjectAssignedResponse;
import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.repository.HeadSubjectBySemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface HDHeadSubjectBySemesterExtendRepository extends HeadSubjectBySemesterRepository {

//    @Query(
//            value = """
//                    SELECT
//                        ROW_NUMBER() OVER (ORDER BY s.id DESC) as orderNumber,
//                        s.id as id,
//                        s.staff_code as staffCode,
//                        s.name as staffName,
//                        s.account_fe as accountFE,
//                        s.account_fpt as accountFpt,
//                        GROUP_CONCAT(DISTINCT CONCAT(sb.subject_code, ' - ', sb.name) SEPARATOR ', ') as subjectsAssigned,
//                        MAX(CONCAT(se.name, ' - ', se.year)) as semesterInfo
//                    FROM
//                        staff s
//                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
//                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_staff = s.id AND hsbs.id_semester = :#{#request.semesterId}
//                    LEFT JOIN subject sb ON hsbs.id_subject = sb.id
//                    LEFT JOIN semester se ON hsbs.id_semester = se.id
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId} AND s.id != :#{#request.currentUserId}
//                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
//                    GROUP BY s.id, s.staff_code, s.name, s.account_fe, s.account_fpt
//                    """,
//            countQuery = """
//                    SELECT
//                        COUNT(DISTINCT s.id)
//                    FROM
//                        staff s
//                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId} AND s.id != :#{#request.currentUserId}
//                        AND (:#{#request.q} IS NULL OR s.name LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.account_fe LIKE CONCAT('%', :#{#request.q}, '%'))
//                        AND (:#{#request.q} IS NULL OR s.account_fpt LIKE CONCAT('%', :#{#request.q}, '%'))
//                    """,
//            nativeQuery = true
//    )
//    Page<HeadSubjectResponse> getHeadSubjects(Pageable pageable, HeadSubjectRequest request);
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
