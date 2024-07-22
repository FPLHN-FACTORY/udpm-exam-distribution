package fplhn.udpm.examdistribution.core.headdepartment.examshift.repository;

import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.request.ExamShiftRequest;
import fplhn.udpm.examdistribution.core.headdepartment.examshift.model.response.ExamShiftResponse;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HDExamShiftRepository extends ExamShiftRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() over (ORDER BY es.exam_date DESC) AS orderNumber,
                        es.id AS id,
                        cs.class_subject_code AS classSubjectCode,
                        CONCAT(s.subject_code, ' - ', s.name) AS subjectInfo,
                        CONCAT(stf.staff_code, ' - ', stf.name) AS firstSupervisor,
                        CONCAT(sts.staff_code, ' - ', sts.name) AS secondSupervisor,
                        es.exam_shift_code AS joinCode,
                        es.room AS room,
                        es.exam_date AS examDate,
                        es.shift AS shift,
                        IF(es.exam_date < UNIX_TIMESTAMP(NOW()), 1, 0) AS isCanEdit
                    FROM exam_shift es
                    LEFT JOIN class_subject cs ON es.id_subject_class = cs.id
                    LEFT JOIN subject s ON cs.id_subject = s.id
                    LEFT JOIN staff stf ON es.id_first_supervisor = stf.id
                    LEFT JOIN staff sts ON es.id_second_supervisor = sts.id
                    WHERE
                        (:#{#request.classSubjectCode} IS NULL OR cs.class_subject_code LIKE CONCAT('%', TRIM(:#{#request.classSubjectCode}), '%')) AND
                        (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', TRIM(:#{#request.subjectCode}), '%')) AND
                        (:#{#request.staffCode} IS NULL OR stf.staff_code LIKE CONCAT('%', TRIM(:#{#request.staffCode}), '%')) AND
                        (:#{#request.staffCode} IS NULL OR sts.staff_code LIKE CONCAT('%', TRIM(:#{#request.staffCode}), '%')) AND
                        (:#{#request.room} IS NULL OR es.room LIKE CONCAT('%', TRIM(:#{#request.room}), '%')) AND
                        (:#{#request.shift} IS NULL OR es.shift = :#{#request.shift}) AND
                         es.exam_date >= :startRangeTime AND es.exam_date <= :endRangeTime
                    """,
            countQuery = """
                    SELECT COUNT(es.id)
                    FROM exam_shift es
                    LEFT JOIN class_subject cs ON es.id_subject_class = cs.id
                    LEFT JOIN subject s ON cs.id_subject = s.id
                    LEFT JOIN staff stf ON es.id_first_supervisor = stf.id
                    LEFT JOIN staff sts ON es.id_second_supervisor = sts.id
                    WHERE
                        (:#{#request.classSubjectCode} IS NULL OR cs.class_subject_code LIKE CONCAT('%', TRIM(:#{#request.classSubjectCode}), '%')) AND
                        (:#{#request.subjectCode} IS NULL OR s.subject_code LIKE CONCAT('%', TRIM(:#{#request.subjectCode}), '%')) AND
                        (:#{#request.staffCode} IS NULL OR stf.staff_code LIKE CONCAT('%', TRIM(:#{#request.staffCode}), '%')) AND
                        (:#{#request.staffCode} IS NULL OR sts.staff_code LIKE CONCAT('%', TRIM(:#{#request.staffCode}), '%')) AND
                        (:#{#request.room} IS NULL OR es.room LIKE CONCAT('%', TRIM(:#{#request.room}), '%')) AND
                        (:#{#request.shift} IS NULL OR es.shift = :#{#request.shift}) AND
                         es.exam_date >= :startRangeTime AND es.exam_date <= :endRangeTime
                    """,
            nativeQuery = true
    )
    Page<ExamShiftResponse> getAllExamShifts(
            Pageable pageable,
            ExamShiftRequest request,
            Long startRangeTime,
            Long endRangeTime
    );

}
