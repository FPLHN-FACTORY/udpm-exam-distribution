package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDAllExamShiftResponse;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.model.response.HDExamShiftResponse;
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
            	es.status as status
            FROM
            	exam_shift es
            LEFT JOIN staff s ON
            	es.id_first_supervisor = s.id
            WHERE
            	s.id_department_facility = :departmentFacilityId
            """, nativeQuery = true)
    List<HDAllExamShiftResponse> getAllExamShift(String departmentFacilityId);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            LEFT JOIN staff s ON
            	es.id_first_supervisor = s.id
            WHERE
                es.exam_shift_code = :examShiftCode
                AND s.id_department_facility = :departmentFacilityId
            """, nativeQuery = true)
    Optional<HDExamShiftResponse> getExamShiftByRequest(String examShiftCode, String departmentFacilityId);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            WHERE es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    HDExamShiftResponse getExamShiftByCode(String examShiftCode);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	student_exam_shift ses
            JOIN exam_shift es ON
            	ses.id_exam_shift = es.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    Integer countStudentInExamShift(String examShiftCode);

}
