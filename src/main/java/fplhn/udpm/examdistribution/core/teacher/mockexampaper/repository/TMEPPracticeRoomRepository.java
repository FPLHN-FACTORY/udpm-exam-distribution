package fplhn.udpm.examdistribution.core.teacher.mockexampaper.repository;

import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMEPStudentRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TMEPPracticeRoomResponse;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.response.TMEPStudentResponse;
import fplhn.udpm.examdistribution.entity.PracticeRoom;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.PracticeRoomRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TMEPPracticeRoomRepository extends PracticeRoomRepository {

    @Query(value = """
                SELECT CONCAT(s.subject_code ,' - ',s.name) AS subjectName,
                	   pr.start_date AS startDate,
                	   pr.end_date AS endDate,
                	   pr.password AS password,
                	   pr.practice_room_code AS practiceRoomCode
                FROM practice_room pr
                JOIN subject s ON s.id = pr.id_subject
                WHERE pr.id_staff LIKE :staffId
                AND pr.id LIKE :id
                AND pr.status = 0
            """, nativeQuery = true)
    Optional<TMEPPracticeRoomResponse> detail(String id, String staffId);

    @Query("""
            SELECT pr FROM PracticeRoom pr
            WHERE pr.endDate < :endTime
            AND pr.status = :status
            """)
    List<PracticeRoom> getAllByEndDate(@Param("endTime") Long endTime, @Param("status") EntityStatus status);

    @Query(value = """
            SELECT s.id AS studentId,
            	   s.name AS studentName,
            	   s.student_code AS studentCode,
            	   s.email AS studentEmail,
            	   pr.practice_room_code AS practiceRoomCode,
            	   spr.joined_at AS joinedAt
            FROM student_practice_room spr
            JOIN practice_room pr ON pr.id = spr.id_practice_room
            JOIN student s ON s.id = spr.id_student
            WHERE spr.id_practice_room LIKE :#{#request.practiceRoomId}
            AND pr.id_staff LIKE :currentTeacher
            AND (:#{#request.keyWord} IS NULL 
                     OR s.name LIKE :#{"%" +#request.keyWord+"%"}
                     OR s.email LIKE :#{"%" +#request.keyWord+"%"}
                     OR s.student_code LIKE :#{"%" +#request.keyWord+"%"})
            ORDER BY spr.joined_at DESC
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM student_practice_room spr
                    JOIN practice_room pr ON pr.id = spr.id_practice_room
                    JOIN student s ON s.id = spr.id_student
                    WHERE spr.id_practice_room LIKE :#{#request.practiceRoomId}
                    AND pr.id_staff LIKE :currentTeacher
                    AND (:#{#request.keyWord} IS NULL 
                             OR s.name LIKE :#{"%" +#request.keyWord+"%"}
                             OR s.email LIKE :#{"%" +#request.keyWord+"%"}
                             OR s.student_code LIKE :#{"%" +#request.keyWord+"%"})
                    ORDER BY spr.joined_at DESC
                    """, nativeQuery = true)
    Page<TMEPStudentResponse> getStudents(Pageable pageable, String currentTeacher, TMEPStudentRequest request);

}
