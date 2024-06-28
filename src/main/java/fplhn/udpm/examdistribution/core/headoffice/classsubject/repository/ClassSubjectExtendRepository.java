package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectKeywordRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.request.ClassSubjectRequest;
import fplhn.udpm.examdistribution.core.headoffice.classsubject.model.response.ClassSubjectResponse;
import fplhn.udpm.examdistribution.entity.Block;
import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.entity.Subject;
import fplhn.udpm.examdistribution.repository.ClassSubjectRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClassSubjectExtendRepository extends ClassSubjectRepository {

    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER (ORDER BY cs.created_date DESC) as orderNumber,
                    cs.id AS id,
                    cs.class_subject_code AS classSubjectCode,
                    sb.subject_code AS subjectCode,
                    CONCAT(sb.subject_code, ' - ', sb.name) AS subjectName,
                    cs.shift AS shift,
                    cs.`day` AS `day`,
                    st.staff_code AS staffCode,
                    st.name AS staffName,
                    CONCAT(b.name, ' - ', s.year, ' - ', s.name) AS blockName,
                    fc.name AS facilityChildName,
                    s.name AS semesterName,
                    s.`year` AS `year`
                FROM
                    class_subject cs
                JOIN block b ON
                    cs.id_block = b.id
                JOIN semester s ON
                    b.id_semester = s.id
                JOIN facility_child fc ON
                    cs.id_facility_child  = fc.id
                JOIN subject sb ON
                    cs.id_subject = sb.id
                JOIN staff st ON
                    cs.id_staff = st.id
                WHERE
                    (fc.id LIKE CONCAT('%',:#{#request.facilityChildId},'%') OR :#{#request.facilityChildId} IS NULL)
                    AND (sb.name LIKE CONCAT('%',:#{#request.subjectName},'%') OR :#{#request.subjectName} IS NULL)
                    AND (st.name LIKE CONCAT('%',:#{#request.staffName},'%') OR :#{#request.staffName} IS NULL)
                    AND (cs.day >= :#{#request.startDate} OR :#{#request.startDate} IS NULL)
                    AND (cs.day <= :#{#request.endDate} OR :#{#request.endDate} IS NULL)
                    AND (cs.shift LIKE CONCAT('%',:#{#request.shift},'%') OR :#{#request.shift} IS NULL)
                    AND (cs.class_subject_code LIKE CONCAT('%',:#{#request.classSubjectCode},'%') 
                    OR :#{#request.classSubjectCode} IS NULL)
                    ORDER BY cs.created_date DESC
            """,
            countQuery = """
                        SELECT
                            COUNT(DISTINCT cs.id)
                        FROM
                            class_subject cs
                        JOIN block b ON
                            cs.id_block = b.id
                        JOIN semester s ON
                            b.id_semester = s.id
                        JOIN facility_child fc ON
                            cs.id_facility_child  = fc.id
                        JOIN subject sb ON
                            cs.id_subject = sb.id
                        JOIN staff st ON
                            cs.id_staff = st.id
                        WHERE
                            (fc.id LIKE CONCAT('%',:#{#request.facilityChildId},'%') OR :#{#request.facilityChildId} IS NULL)
                            AND (sb.name LIKE CONCAT('%',:#{#request.subjectName},'%') OR :#{#request.subjectName} IS NULL)
                            AND (st.name LIKE CONCAT('%',:#{#request.staffName},'%') OR :#{#request.staffName} IS NULL)
                            AND (cs.day >= :#{#request.startDate} OR :#{#request.startDate} IS NULL)
                            AND (cs.day <= :#{#request.endDate} OR :#{#request.endDate} IS NULL)
                            AND (cs.shift LIKE CONCAT('%',:#{#request.shift},'%') OR :#{#request.shift} IS NULL)
                            AND (cs.class_subject_code LIKE CONCAT('%',:#{#request.classSubjectCode},'%') 
                            OR :#{#request.classSubjectCode} IS NULL)
                            ORDER BY cs.created_date DESC
                    """
            , nativeQuery = true)
    Page<ClassSubjectResponse> getSearchClassSubject(Pageable pageable, ClassSubjectRequest request);

    @Query(value = """
                SELECT
                    ROW_NUMBER() OVER (ORDER BY cs.created_date DESC) as orderNumber,
                    cs.id AS id,
                    cs.class_subject_code AS classSubjectCode,
                    sb.subject_code AS subjectCode,
                    CONCAT(sb.subject_code, ' - ', sb.name) AS subjectName,
                    cs.shift AS shift,
                    cs.`day` AS `day`,
                    st.staff_code AS staffCode,
                    st.name AS staffName,
                    CONCAT(b.name, ' - ', s.year, ' - ', s.name) AS blockName,
                    fc.name AS facilityChildName,
                    s.name AS semesterName,
                    s.`year` AS `year`
                FROM
                    class_subject cs
                JOIN block b ON
                    cs.id_block = b.id
                JOIN semester s ON
                    b.id_semester = s.id
                JOIN facility_child fc ON
                    cs.id_facility_child  = fc.id
                JOIN subject sb ON
                    cs.id_subject = sb.id
                JOIN staff st ON
                    cs.id_staff = st.id
                WHERE
                    (:#{#request.keyword} IS NULL OR fc.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                    OR sb.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                    OR st.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                    OR cs.shift LIKE CONCAT('%',:#{#request.keyword},'%')
                    OR cs.class_subject_code LIKE CONCAT('%',:#{#request.classSubjectCode},'%') 
                    AND (cs.day >= :#{#request.startDate} OR :#{#request.startDate} IS NULL)
                    ORDER BY cs.created_date DESC
            """,
            countQuery = """
                        SELECT
                            COUNT(DISTINCT cs.id)
                        FROM
                            class_subject cs
                        JOIN block b ON
                            cs.id_block = b.id
                        JOIN semester s ON
                            b.id_semester = s.id
                        JOIN facility_child fc ON
                            cs.id_facility_child  = fc.id
                        JOIN subject sb ON
                            cs.id_subject = sb.id
                        JOIN staff st ON
                            cs.id_staff = st.id
                        WHERE
                            (:#{#request.keyword} IS NULL OR fc.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                            OR sb.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                            OR st.name LIKE CONCAT('%',:#{#request.keyword},'%') 
                            OR cs.shift LIKE CONCAT('%',:#{#request.keyword},'%')
                            OR cs.class_subject_code LIKE CONCAT('%',:#{#request.classSubjectCode},'%') 
                            AND (cs.day >= :#{#request.startDate} OR :#{#request.startDate} IS NULL)
                            ORDER BY cs.created_date DESC
                    """
            , nativeQuery = true)
    Page<ClassSubjectResponse> getSearchClassSubjectByKeyword(Pageable pageable, ClassSubjectKeywordRequest request);

    Optional<ClassSubject> findByClassSubjectCode(String classSubjectCode);

    Optional<ClassSubject> findByClassSubjectCodeAndBlockAndFacilityChildAndSubject(
            String classSubjectCode,
            Block block,
            FacilityChild facilityChild,
            Subject subject);

    @Query(value = """
            SELECT
                    COUNT(DISTINCT cs.id)
                FROM
                    class_subject cs
                JOIN block b ON
                    cs.id_block = b.id
                JOIN semester s ON
                    b.id_semester = s.id
                JOIN facility_child fc ON
                    cs.id_facility_child  = fc.id
                JOIN subject sb ON
                    cs.id_subject = sb.id
                JOIN staff st ON
                    cs.id_staff = st.id 
                WHERE cs.class_subject_code = :#{#classSubjectCode}
            """, nativeQuery = true)
    Integer duplicateClassSubjectCode(String classSubjectCode);

    @Query(value = """
            SELECT
                    cs.id AS id,
                    cs.class_subject_code AS classSubjectCode,
                    sb.subject_code AS subjectCode,
                    sb.subject_code + ' - ' + sb.name AS subjectName,
                    cs.shift AS shift,
                    cs.`day` AS `day`,
                    st.staff_code AS staffCode,
                    st.name AS staffName,
                    b.id AS blockId,
                    CONCAT(b.name, ' - ', s.year, ' - ', s.name) AS blockName,
                    fc.id AS facilityChildId,
                    fc.name AS facilityChildName,
                    s.name AS semesterName,
                    s.`year` AS `year`
                FROM
                    class_subject cs
                JOIN block b ON
                    cs.id_block = b.id
                JOIN semester s ON
                    b.id_semester = s.id
                JOIN facility_child fc ON
                    cs.id_facility_child  = fc.id
                JOIN subject sb ON
                    cs.id_subject = sb.id
                JOIN staff st ON
                    cs.id_staff = st.id
                WHERE cs.id = :#{#classSubjectId}
            """, nativeQuery = true)
    Optional<ClassSubjectResponse> getDetailClassSubject(String classSubjectId);
}
