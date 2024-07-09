package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffsBySubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectsStaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.StaffsBySubjectResponse;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SubjectAssignedResponse;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SubjectsStaffResponse;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HDSubjectExtendRepository extends SubjectRepository {

//    @Query(
//            value = """
//                    SELECT
//                         s.id AS id,
//                         s.subject_code AS subjectCode,
//                         s.name AS subjectName,
//                         s.subject_type AS subjectType,
//                         CASE WHEN s.id_head_subject = :#{#request.staffId} THEN 1 ELSE 0 END AS assigned
//                    FROM
//                         subject s
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
//                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
//                        AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE CONCAT('%', :#{#request.subjectType}, '%'))
//                    """,
//            countQuery = """
//                    SELECT COUNT(*)
//                    FROM subject s
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
//                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
//                        AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE CONCAT('%', :#{#request.subjectType}, '%'))
//                    """,
//            nativeQuery = true
//    )
//    Page<SubjectAssignedResponse> getSubjectAssigned(SubjectAssignedRequest request, Pageable pageable);

//    List<Subject> findByHeadSubject(Staff staff);

//    @Query(
//            value = """
//                    SELECT
//                        ROW_NUMBER() OVER (ORDER BY s.id) AS orderNumber,
//                        s.id AS id,
//                        s.subject_code AS subjectCode,
//                        s.name AS subjectName,
//                        s.subject_type AS subjectType,
//                        CONCAT(staff.staff_code, ' - ', staff.name) AS staffInChargeInfo
//                    FROM
//                        subject s
//                    LEFT JOIN staff ON s.id_head_subject = staff.id
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
//                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
//                        AND (:#{#request.staffCode} IS NULL OR staff.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
//                        AND (:#{#request.staffName} IS NULL OR staff.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
//                    """,
//            countQuery = """
//                    SELECT COUNT(*)
//                    FROM subject s
//                    LEFT JOIN staff ON s.id_head_subject = staff.id
//                    LEFT JOIN department d ON s.id_department = d.id
//                    LEFT JOIN department_facility df ON d.id = df.id_department
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
//                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
//                        AND (:#{#request.staffCode} IS NULL OR staff.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
//                        AND (:#{#request.staffName} IS NULL OR staff.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
//                    """,
//            nativeQuery = true
//    )
//    Page<SubjectsStaffResponse> getSubjectsStaff(SubjectsStaffRequest request, Pageable pageable);

//    @Query(
//            value = """
//                    SELECT
//                        s.id AS id,
//                        s.staff_code AS staffCode,
//                        s.name AS staffName,
//                        s.account_fpt AS accountFPT,
//                        s.account_fe AS accountFE,
//                        IF(sub.id = :#{#request.subjectId} AND s.id = sub.id_head_subject, 1, 0) AS assigned
//                    FROM staff s
//                    LEFT JOIN subject sub ON s.id = sub.id_head_subject AND sub.id = :#{#request.subjectId}
//                    JOIN department_facility df ON s.id_department_facility = df.id
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.staffCode} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
//                        AND (:#{#request.staffName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
//                    """,
//            countQuery = """
//                    SELECT COUNT(*)
//                    FROM staff s
//                    LEFT JOIN subject sub ON s.id = sub.id_head_subject AND sub.id = :#{#request.subjectId}
//                    JOIN department_facility df ON s.id_department_facility = df.id
//                    WHERE
//                        df.id = :#{#request.departmentFacilityId}
//                        AND (:#{#request.staffCode} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
//                        AND (:#{#request.staffName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
//                    """,
//            nativeQuery = true)
//    Page<StaffsBySubjectResponse> getStaffsBySubject(StaffsBySubjectRequest request, Pageable pageable);

}
