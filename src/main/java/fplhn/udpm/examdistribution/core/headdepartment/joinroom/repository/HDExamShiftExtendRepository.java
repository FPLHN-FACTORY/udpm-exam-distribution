package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDAllExamShiftResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HDExamShiftExtendRepository extends ExamShiftRepository {

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode,
            	es.room as room,
            	s.staff_code as codeFirstSupervisor,
            	s.name as nameFirstSupervisor,
            	es.exam_shift_status as status
            FROM
            	exam_shift es
            JOIN staff s ON
            	es.id_first_supervisor = s.id
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s2 ON
            	cs.id_subject = s2.id
            JOIN subject_by_subject_group sbsg ON
             	sbsg.id_subject = s2.id
            JOIN subject_group sg ON
            	sbsg.id_subject_group = sg.id
            WHERE
            	sg.id_department_facility = :departmentFacilityId
            	AND es.exam_date >= :currentDate
            	AND es.shift = :currentShift
            	AND es.exam_shift_status IN ('NOT_STARTED', 'IN_PROGRESS')
            """, nativeQuery = true)
    List<HDAllExamShiftResponse> getAllExamShift(String departmentFacilityId, Long currentDate, String currentShift);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            JOIN staff s ON
            	es.id_first_supervisor = s.id
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s2 ON
            	cs.id_subject = s2.id
            JOIN subject_by_subject_group sbsg ON
             	sbsg.id_subject = s2.id
            JOIN subject_group sg ON
            	sbsg.id_subject_group = sg.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            	AND sg.id_department_facility = :departmentFacilityId
            """, nativeQuery = true)
    Optional<HDExamShiftResponse> getExamShiftByRequest(String examShiftCode, String departmentFacilityId);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode,
            	s.path_exam_rule as pathExamRule
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s ON
            	cs.id_subject = s.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    HDExamShiftResponse getExamShiftByCode(String examShiftCode);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN block b ON
            	cs.id_block = b.id
            JOIN facility_child fc ON
            	cs.id_facility_child = fc.id
            WHERE
            	cs.id = :classSubjectId
            """, nativeQuery = true)
    Integer countByClassSubjectId(String classSubjectId);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            WHERE
            	es.exam_date = :examDate
            AND es.shift = :shift
            AND es.room = :room
            """, nativeQuery = true)
    Integer countByExamDateAndShiftAndRoom(Long examDate, String shift, String room);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
                ses.exam_student_status IN(0, 1, 2)
            	AND es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Integer countStudentInExamShift(String examShiftCode);

}
