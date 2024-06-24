package fplhn.udpm.examdistribution.core.headoffice.semester.repository;

import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.response.SemesterResponse;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SemesterExtendRepository extends SemesterRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY s.id DESC ) as orderNumber,
                        s.id as id,
                        s.name as semesterName,
                        s.year as semesterYear,
                        s.status as semesterStatus,
                        s.start_time as startTime
                    FROM
                        semester s
                    WHERE
                        (:#{#request.semesterName} IS NULL OR s.name LIKE CONCAT('%',:#{#request.semesterName},'%'))
                        AND (:#{#request.semesterYear} IS NULL OR s.year = :#{#request.semesterYear})
                        AND (:#{#request.startDate} IS NULL OR s.start_time >= :#{#request.startDate})
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT s.id)
                    FROM
                        semester s
                    WHERE
                        (:#{#request.semesterName} IS NULL OR s.name LIKE CONCAT('%',:#{#request.semesterName},'%'))
                        AND (:#{#request.semesterYear} IS NULL OR s.year = :#{#request.semesterYear})
                        AND (:#{#request.startDate} IS NULL OR s.start_time >= :#{#request.startDate})
                    """,
            nativeQuery = true
    )
    Page<SemesterResponse> getAllSemester(Pageable pageable, SemesterRequest request);

    @Query(
            value = """
                    SELECT
                        s.id,
                        s.name,
                        s.year,
                        s.status,
                        s.start_time,
                        s.created_date,
                        s.last_modified_date
                    FROM
                        semester s
                    WHERE
                        s.name = :semesterName
                    AND s.year = :semesterYear
                    AND s.status = 'ACTIVE'
                    """, nativeQuery = true
    )
    Optional<Semester> existingBySemesterNameAndSemesterYear(String semesterName, Integer semesterYear);

    @Query(
            value = """
                    SELECT
                        s.id as id,
                        s.name as semesterName,
                        s.year as semesterYear,
                        s.status as semesterStatus,
                        s.start_time as startTime
                    FROM
                        semester s
                    WHERE
                        s.id = :semesterId
                    """, nativeQuery = true
    )
    Optional<SemesterResponse> getDetailSemesterById(String semesterId);
}
