package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.request.HSExamShiftAndInfoRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response.HSAllExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.model.response.HSExamShiftResponse;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HSExamShiftExtendRepository extends ExamShiftRepository {

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
            LEFT JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            LEFT JOIN subject s2 ON
            	cs.id_subject = s2.id
            LEFT JOIN department d ON
            	s2.id_department = d.id
            LEFT JOIN department_facility df ON
            	d.id = df.id_department
            WHERE
            	df.id_staff = :#{#hsExamShiftAndInfoRequest.staffId}
            	AND df.id_department = :#{#hsExamShiftAndInfoRequest.departmentId}
            	AND df.id_facility = :#{#hsExamShiftAndInfoRequest.facilityId}
            """, nativeQuery = true)
    List<HSAllExamShiftResponse> getAllExamShift(HSExamShiftAndInfoRequest hsExamShiftAndInfoRequest);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            LEFT JOIN staff s ON
            	es.id_first_supervisor = s.id
            LEFT JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            LEFT JOIN subject s2 ON
            	cs.id_subject = s2.id
            LEFT JOIN department d ON
            	s2.id_department = d.id
            LEFT JOIN department_facility df ON
            	d.id = df.id_department
            WHERE
                es.exam_shift_code = :examShiftCode
            	AND df.id_staff = :#{#hsExamShiftAndInfoRequest.staffId}
            	AND df.id_department = :#{#hsExamShiftAndInfoRequest.departmentId}
            	AND df.id_facility = :#{#hsExamShiftAndInfoRequest.facilityId}
            """, nativeQuery = true)
    Optional<HSExamShiftResponse> getExamShiftByRequest(String examShiftCode,
                                                        HSExamShiftAndInfoRequest hsExamShiftAndInfoRequest);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.id as id,
            	es.exam_shift_code as examShiftCode
            FROM
            	exam_shift es
            WHERE es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    HSExamShiftResponse getExamShiftByCode(String examShiftCode);

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
