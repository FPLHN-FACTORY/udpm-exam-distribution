package fplhn.udpm.examdistribution.core.headoffice.semester.repository;

import fplhn.udpm.examdistribution.core.headoffice.semester.model.request.SemesterRequest;
import fplhn.udpm.examdistribution.core.headoffice.semester.model.response.SemesterResponse;
import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SemesterExtendRepository extends SemesterRepository {

    @Query(
            value = """
                    SELECT s.id AS semesterId,
                         CONCAT(s.name, ' - ', s.year) AS semesterName,
                         s.start_time AS startTime,
                         s.end_time AS endTime,
                         b1.start_time AS startTimeBlock1,
                         b1.end_time AS endTimeBlock1,
                         b2.start_time AS startTimeBlock2,
                         b2.end_time AS endTimeBlock2
                    FROM semester s
                    LEFT JOIN block b1 ON b1.id_semester = s.id AND b1.name = 'BLOCK_1'
                    LEFT JOIN block b2 ON b2.id_semester = s.id AND b2.name = 'BLOCK_2'
                    WHERE
                        (:#{#request.semesterName} IS NULL OR CONCAT(s.name, ' - ', s.year) LIKE CONCAT('%',:#{#request.semesterName},'%'))
                        AND (:#{#request.startDateSemester} IS NULL 
                                 OR :#{#request.endDateSemester} IS NULL
                                 OR (s.start_time >= :#{#request.startDateSemester} AND s.start_time <= :#{#request.endDateSemester})
                                 OR (s.end_time >= :#{#request.startDateSemester} AND s.start_time <= :#{#request.endDateSemester})
                                 OR (:#{#request.startDateSemester} >= s.start_time AND :#{#request.startDateSemester} <= s.end_time)
                                 OR (:#{#request.startDateSemester} >= s.start_time AND :#{#request.endDateSemester} <= s.end_time))
                        AND (:#{#request.startDateBlock} IS NULL
                                 OR :#{#request.endDateBlock} IS NULL
                                 OR (b1.start_time >= :#{#request.startDateBlock} AND b1.start_time <= :#{#request.endDateBlock})
                                 OR (b1.end_time >= :#{#request.startDateBlock} AND b1.end_time <= :#{#request.endDateBlock})
                                 OR (b2.start_time >= :#{#request.startDateBlock} AND b2.start_time <= :#{#request.endDateBlock})
                                 OR (b2.end_time >= :#{#request.startDateBlock} AND b2.end_time <= :#{#request.endDateBlock})
                                 OR (:#{#request.startDateBlock} >= b1.start_time AND :#{#request.startDateBlock} <= b1.end_time)
                                 OR (:#{#request.startDateBlock} >= b2.start_time AND :#{#request.startDateBlock} <= b2.end_time)
                                 OR (:#{#request.endDateBlock} >= b1.start_time AND :#{#request.endDateBlock} <= b1.end_time)
                                 OR (:#{#request.endDateBlock} >= b2.start_time AND :#{#request.endDateBlock} <= b2.end_time))
                    ORDER BY s.created_date DESC
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT s.id)
                    FROM semester s
                    LEFT JOIN block b1 ON b1.id_semester = s.id AND b1.name = 'BLOCK_1'
                    LEFT JOIN block b2 ON b2.id_semester = s.id AND b2.name = 'BLOCK_2'
                    WHERE
                        (:#{#request.semesterName} IS NULL OR s.name LIKE CONCAT('%',:#{#request.semesterName},'%'))
                        AND (:#{#request.startDateSemester} IS NULL 
                                 OR :#{#request.endDateSemester} IS NULL
                                 OR (s.start_time >= :#{#request.startDateSemester} AND s.start_time <= :#{#request.endDateSemester})
                                 OR (s.end_time >= :#{#request.startDateSemester} AND s.start_time <= :#{#request.endDateSemester})
                                 OR (:#{#request.startDateSemester} >= s.start_time AND :#{#request.startDateSemester} <= s.end_time)
                                 OR (:#{#request.startDateSemester} >= s.start_time AND :#{#request.endDateSemester} <= s.end_time))
                        AND (:#{#request.startDateBlock} IS NULL
                                 OR :#{#request.endDateBlock} IS NULL
                                 OR (b1.start_time >= :#{#request.startDateBlock} AND b1.start_time <= :#{#request.endDateBlock})
                                 OR (b1.end_time >= :#{#request.startDateBlock} AND b1.end_time <= :#{#request.endDateBlock})
                                 OR (b2.start_time >= :#{#request.startDateBlock} AND b2.start_time <= :#{#request.endDateBlock})
                                 OR (b2.end_time >= :#{#request.startDateBlock} AND b2.end_time <= :#{#request.endDateBlock})
                                 OR (:#{#request.startDateBlock} >= b1.start_time AND :#{#request.startDateBlock} <= b1.end_time)
                                 OR (:#{#request.startDateBlock} >= b2.start_time AND :#{#request.startDateBlock} <= b2.end_time)
                                 OR (:#{#request.endDateBlock} >= b1.start_time AND :#{#request.endDateBlock} <= b1.end_time)
                                 OR (:#{#request.endDateBlock} >= b2.start_time AND :#{#request.endDateBlock} <= b2.end_time))
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
                        s.end_time,
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
                    SELECT s.id AS semesterId,
                         s.name AS semesterName,
                         s.start_time AS startTime,
                         s.end_time AS endTime,
                         b1.start_time AS startTimeBlock1,
                         b1.end_time AS endTimeBlock1,
                         b2.start_time AS startTimeBlock2,
                         b2.end_time AS endTimeBlock2
                    FROM semester s
                    LEFT JOIN block b1 ON b1.id_semester = s.id AND b1.name = 'BLOCK_1'
                    LEFT JOIN block b2 ON b2.id_semester = s.id AND b2.name = 'BLOCK_2'
                    WHERE s.id = :semesterId
                    """, nativeQuery = true
    )
    Optional<SemesterResponse> getDetailSemesterById(String semesterId);

    @Query("""
     SELECT s
     FROM Semester s
     WHERE (s.startTime >= :startTime AND s.startTime <= :endTime)
     OR (s.endTime >= :startTime AND s.endTime <= :endTime)
     OR (:startTime >= s.startTime AND :startTime <= s.endTime)
     OR (:endTime >= s.startTime AND :endTime <= s.endTime)
    """)
    List<Semester> checkConflictTime(Long startTime, Long endTime);

}
