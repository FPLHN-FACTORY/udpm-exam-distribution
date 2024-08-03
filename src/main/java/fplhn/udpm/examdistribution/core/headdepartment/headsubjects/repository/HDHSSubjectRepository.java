package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.repository;

import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.request.SubjectByHeadSubjectRequest;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response.SubjectAssignResponse;
import fplhn.udpm.examdistribution.core.headdepartment.headsubjects.model.response.SubjectResponse;
import fplhn.udpm.examdistribution.repository.SubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HDHSSubjectRepository extends SubjectRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY s.id DESC ) as orderNumber,
                        s.id as id,
                        s.subject_code as subjectCode,
                        s.name as subjectName,
                        s.subject_type as subjectType
                    FROM
                        subject s
                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_subject = s.id
                    WHERE
                        hsbs.id_staff = :#{#request.headSubjectId} AND hsbs.id_semester = :#{#request.currentSemesterId}
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT s.id)
                    FROM
                        subject s
                    LEFT JOIN head_subject_by_semester hsbs ON hsbs.id_subject = s.id
                    WHERE
                        hsbs.id_staff = :#{#request.headSubjectId} AND hsbs.id_semester = :#{#request.currentSemesterId}
                    """,
            nativeQuery = true
    )
    Page<SubjectResponse> getSubjectByHeadSubject(Pageable pageable, SubjectByHeadSubjectRequest request);

    @Query(
            value = """
                    SELECT
                    ROW_NUMBER() OVER (
                            ORDER BY
                                IF(hsbs.id_staff IS NOT NULL, 0, 1),
                                s.id DESC
                        ) as orderNumber,
                      s.id AS id,
                      s.subject_code AS subjectCode,
                      s.name AS subjectName,
                      s.subject_type AS subjectType,
                      IF((hsbs.id_staff = :#{#request.headSubjectId}
                              AND hsbs.id_semester = :#{#request.currentSemesterId}), 1, 0) AS isAssigned
                    FROM
                      subject s
                    LEFT JOIN head_subject_by_semester hsbs ON
                      s.id = hsbs.id_subject
                    LEFT JOIN staff st ON
                      hsbs.id_staff = st.id
                    LEFT JOIN department d ON
                      s.id_department = d.id
                    LEFT JOIN department_facility df ON
                      d.id = df.id_department
                    WHERE
                      df.id = :#{#request.departmentFacilityId}
                      AND (:#{#request.q} IS NULL OR (
                          s.subject_code LIKE CONCAT('%',:#{#request.q},'%')
                          OR s.name LIKE CONCAT('%',:#{#request.q},'%')
                      ))
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT s.id)
                    FROM
                      subject s
                    LEFT JOIN head_subject_by_semester hsbs ON
                        s.id = hsbs.id_subject
                    LEFT JOIN staff st ON
                        hsbs.id_staff = st.id
                    LEFT JOIN department d ON
                        s.id_department = d.id
                    LEFT JOIN department_facility df ON
                        d.id = df.id_department
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.q} IS NULL OR (
                            s.subject_code LIKE CONCAT('%',:#{#request.q},'%')
                            OR s.name LIKE CONCAT('%',:#{#request.q},'%')
                        ))
                    """,
            nativeQuery = true
    )
    Page<SubjectAssignResponse> getSubjectAssign(Pageable pageable, SubjectByHeadSubjectRequest request);

}
