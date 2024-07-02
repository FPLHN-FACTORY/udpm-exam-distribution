package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.SubjectAssignedRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.SubjectAssignedResponse;
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

    @Query(
            value = """
                    SELECT
                         s.id AS id,
                         s.subject_code AS subjectCode,
                         s.name AS subjectName,
                         s.subject_type AS subjectType,
                         CASE WHEN s.id_head_subject = :#{#request.staffId} THEN 1 ELSE 0 END AS assigned
                    FROM
                         subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
                        AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE CONCAT('%', :#{#request.subjectType}, '%'))
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM subject s
                    LEFT JOIN department d ON s.id_department = d.id
                    LEFT JOIN department_facility df ON d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', :#{#request.subjectCode}, '%'))
                        AND (:#{#request.subjectName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.subjectName}, '%'))
                        AND (:#{#request.subjectType} IS NULL OR s.subject_type LIKE CONCAT('%', :#{#request.subjectType}, '%'))
                    """,
            nativeQuery = true
    )
    Page<SubjectAssignedResponse> getSubjectAssigned(SubjectAssignedRequest request, Pageable pageable);

    List<Subject> findByHeadSubject(Staff staff);
}
