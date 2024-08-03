package fplhn.udpm.examdistribution.core.headsubject.joinroom.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSExamShiftRequest;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSAllExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToHeadDepartmentWhenCreateExamShiftResponse;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.response.HSSendMailToSupervisorWhenCreateExamShiftResponse;
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
            	es.shift as shift,
            	es.room as room,
            	cs.class_subject_code as classSubjectCode,
            	s2.name as subjectName,
            	s.staff_code as codeFirstSupervisor,
            	s.name as nameFirstSupervisor,
            	s3.staff_code as codeSecondSupervisor,
            	s3.name as nameSecondSupervisor,
            	es.exam_shift_status as status
            FROM
            	exam_shift es
            JOIN staff s ON
            	es.id_first_supervisor = s.id
            JOIN staff s3 ON
            	es.id_second_supervisor = s3.id
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s2 ON
            	cs.id_subject = s2.id
            JOIN department d ON
                s2.id_department = d.id
            JOIN department_facility df ON
                d.id = df.id_department
            JOIN head_subject_by_semester hsbs ON
            	hsbs.id_subject = s2.id
            WHERE
            	df.id = :#{#hsExamShiftRequest.departmentFacilityId}
                AND hsbs.id_semester = :#{#hsExamShiftRequest.semesterId}
                AND hsbs.id_staff = :#{#hsExamShiftRequest.staffId}
            	AND es.exam_date = :#{#hsExamShiftRequest.currentDate}
            	AND es.shift >= :#{#hsExamShiftRequest.currentShift}
            	AND es.exam_shift_status IN ('NOT_STARTED', 'IN_PROGRESS')
            """, nativeQuery = true)
    List<HSAllExamShiftResponse> getAllExamShift(HSExamShiftRequest hsExamShiftRequest);

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
            JOIN department d ON
                s2.id_department = d.id
            JOIN department_facility df ON
                d.id = df.id_department
            JOIN head_subject_by_semester hsbs ON
            	hsbs.id_subject = s2.id
            WHERE
                es.exam_shift_code = :examShiftCode
                AND df.id = :departmentFacilityId
                AND hsbs.id_semester = :semesterId
            """, nativeQuery = true)
    Optional<HSExamShiftResponse> getExamShiftByRequest(String examShiftCode, String departmentFacilityId, String semesterId);

    @Query(value = """
            SELECT
            	es.*
            FROM
            	exam_shift es
            JOIN staff s ON
            	es.id_first_supervisor = s.id
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s2 ON
            	cs.id_subject = s2.id
            JOIN department d ON
                s2.id_department = d.id
            JOIN department_facility df ON
                d.id = df.id_department
            JOIN head_subject_by_semester hsbs ON
            	hsbs.id_subject = s2.id
            WHERE
                es.exam_shift_code = :examShiftCode
                AND df.id = :departmentFacilityId
                AND hsbs.id_semester = :semesterId
            """, nativeQuery = true)
    Optional<ExamShift> findExamShiftByRequest(String examShiftCode, String departmentFacilityId, String semesterId);

    Optional<ExamShift> findByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            JOIN class_subject cs ON
            	es.id_subject_class = cs.id
            JOIN subject s on
             	cs.id_subject = s.id
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
            	exam_shift es
            WHERE
            	es.exam_date = :examDate
            AND es.shift = :shift
            AND es.id_subject_class = :classSubjectId
            """, nativeQuery = true)
    Integer countByExamDateAndShiftAndClassSubjectId(Long examDate, String shift, String classSubjectId);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            WHERE
            	es.id_first_supervisor = :firstSupervisorId
            	AND es.exam_date = :examDate
            	AND es.shift = :shift
            """, nativeQuery = true)
    Integer countExistingFirstSupervisorByCurrentExamDateAndShift(String firstSupervisorId, Long examDate, String shift);

    @Query(value = """
            SELECT
            	COUNT(*)
            FROM
            	exam_shift es
            WHERE
            	es.id_second_supervisor = :secondSupervisorId
            	AND es.exam_date = :examDate
            	AND es.shift = :shift
            """, nativeQuery = true)
    Integer countExistingSecondSupervisorByCurrentExamDateAndShift(String secondSupervisorId, Long examDate, String shift);

    @Query(value = """
            SELECT
            	es.id as id,
             	es.exam_shift_code as examShiftCode,
             	s.name as subjectName,
             	cs.class_subject_code as classSubjectCode,
             	es.password as password,
             	er.file_id as pathExamRule
            FROM
            	exam_shift es
            JOIN class_subject cs on
            	es.id_subject_class = cs.id
            JOIN subject s on
            	cs.id_subject = s.id
            JOIN exam_rule er on
            	s.id_exam_rule = er.id
            WHERE
            	es.exam_shift_code = :examShiftCode
            """, nativeQuery = true)
    HSExamShiftResponse getExamShiftByCode(String examShiftCode);

    @Query(value = """
            SELECT
            	es.exam_shift_code
            FROM
            	exam_shift es
            """, nativeQuery = true)
    List<String> getAllExamShiftCode();

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

//    @Query(value = """
//            SELECT
//            	es.exam_shift_code as examShiftCode,
//            	es.room as room,
//            	es.exam_date as examDate,
//            	es.shift as shift,
//            	cs.class_subject_code as classSubjectCode,
//            	s3.name as subjectName,
//            	s.name as nameFirstSupervisor,
//            	s.staff_code as codeFirstSupervisor,
//            	s.account_fe as accountFeFirstSupervisor,
//            	s.account_fpt as accountFptFirstSupervisor,
//            	s2.name as nameSecondSupervisor,
//            	s2.staff_code as codeSecondSupervisor,
//            	s2.account_fe as accountFeSecondSupervisor,
//            	s2.account_fpt as accountFptSecondSupervisor
//            FROM
//            	exam_shift es
//            JOIN staff s on
//            	es.id_first_supervisor = s.id
//            JOIN staff s2 on
//            	es.id_second_supervisor = s2.id
//            JOIN class_subject cs on
//            	es.id_subject_class = cs.id
//            JOIN subject s3 on
//            	cs.id_subject = s3.id
//            JOIN department d on
//            	s3.id_department = d.id
//            JOIN department_facility df on
//            	d.id = df.id_department
//            JOIN facility f on
//            	df.id_facility = f.id
//            WHERE
//                es.shift = :shift
//                AND es.exam_date = :examDate
//                AND df.id = :departmentFacilityId
//            """, nativeQuery = true)
//    List<HSSendMailToHeadDepartmentWhenCreateExamShiftResponse>
//    getContentSendMailToHeadDepartment(String shift, Long examDate, String departmentFacilityId);

//    @Query(value = """
//            SELECT
//                es.exam_shift_code as examShiftCode,
//            	es.room as room,
//            	es.exam_date as examDate,
//            	es.shift as shift,
//            	cs.class_subject_code as classSubjectCode,
//            	s3.name as subjectName,
//            	s.name as nameFirstSupervisor,
//            	s.staff_code as codeFirstSupervisor,
//            	s.account_fe as accountFeFirstSupervisor,
//            	s.account_fpt as accountFptFirstSupervisor,
//            	s2.name as nameSecondSupervisor,
//            	s2.staff_code as codeSecondSupervisor,
//            	s2.account_fe as accountFeSecondSupervisor,
//            	s2.account_fpt as accountFptSecondSupervisor
//            FROM
//            	exam_shift es
//            JOIN staff s ON
//            	es.id_first_supervisor = s.id
//            JOIN staff s2 ON
//            	es.id_second_supervisor = s2.id
//            JOIN class_subject cs ON
//            	es.id_subject_class = cs.id
//            JOIN subject s3 ON
//            	cs.id_subject = s3.id
//            WHERE
//                es.exam_shift_code = :examShiftCode
//            """, nativeQuery = true)
//    HSSendMailToSupervisorWhenCreateExamShiftResponse getContentSendMailToSupervisor(String examShiftCode);

}
