package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.request.CEPListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response.CEPListExamPaperResponse;
import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response.CEPListMajorFacilityResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CEPUploadExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT
             	ep.id AS id,
             	ROW_NUMBER() OVER(ORDER BY ep.id DESC) AS orderNumber,
             	ep.path AS fileId,
             	ep.exam_paper_code AS examPaperCode,
             	ep.exam_paper_type AS examPaperType,
             	ep.created_exam_paper_date AS createdDate,
             	ep.exam_paper_status AS status,
             	subj.id AS subjectId,
             	subj.name AS subjectName,
             	m.name AS majorName,
             	ep.is_public AS isPublic,
             	CONCAT(st.name, "-", st.staff_code) AS staffName,
             	f.name AS facilityName,
             	mf.id AS majorFacilityId,
             	(
             	    SELECT
             	        CASE
             	            WHEN COUNT(epbs.id) > 0 THEN 1
             	            ELSE 0
             	        END
             	    FROM exam_paper_by_semester epbs
             	    WHERE epbs.id_semester = :semesterId AND
             	          epbs.id_exam_paper = ep.id AND
             	          epbs.status = 0
             	) AS isChoose
            FROM
                 head_subject_by_semester hsbs
            JOIN subject_group sg ON
                 sg.id = hsbs.id_subject_group
            JOIN subject subj ON
             	subj.id_subject_group = sg.id
            JOIN exam_paper ep ON
                 ep.id_subject = subj.id
             JOIN major_facility mf ON
             	mf.id = ep.id_major_facility
             JOIN major m ON
             	m.id = mf.id_major
             JOIN staff st ON
             	st.id = ep.id_staff_upload
             JOIN department_facility df ON
             	df.id = mf.id_department_facility
             JOIN facility f ON
             	f.id = df.id_facility
             WHERE ep.exam_paper_status <> :examPaperStatus AND
                   ep.exam_paper_type <> :examPaperType AND
                   mf.id_department_facility = :departmentFacilityId AND
                   hsbs.id_staff = :userId AND
                   ep.status = 0 AND
                   hsbs.status = 0 AND
                   (:#{#request.semesterId} IS NULL OR hsbs.id_semester LIKE CONCAT('%', TRIM(:#{#request.semesterId}) ,'%')) AND
                   (:#{#request.blockId} IS NULL OR ep.id_block LIKE CONCAT('%', TRIM(:#{#request.blockId}) ,'%')) AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """, countQuery = """
            SELECT
             	COUNT(hsbs.id)
            FROM
                 head_subject_by_semester hsbs
            JOIN subject_group sg ON
                 sg.id = hsbs.id_subject_group
            JOIN subject subj ON
             	subj.id_subject_group = sg.id
            JOIN exam_paper ep ON
                 ep.id_subject = subj.id
             JOIN major_facility mf ON
             	mf.id = ep.id_major_facility
             JOIN major m ON
             	m.id = mf.id_major
             JOIN staff st ON
             	st.id = ep.id_staff_upload
             JOIN department_facility df ON
             	df.id = mf.id_department_facility
             JOIN facility f ON
             	f.id = df.id_facility
             WHERE ep.exam_paper_status <> :examPaperStatus AND
                   ep.exam_paper_type <> :examPaperType AND
                   mf.id_department_facility = :departmentFacilityId AND
                   hsbs.id_staff = :userId AND
                   ep.status = 0 AND
                   hsbs.status = 0 AND
                   (:#{#request.semesterId} IS NULL OR hsbs.id_semester LIKE CONCAT('%', TRIM(:#{#request.semesterId}) ,'%')) AND
                   (:#{#request.blockId} IS NULL OR ep.id_block LIKE CONCAT('%', TRIM(:#{#request.blockId}) ,'%')) AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """, nativeQuery = true)
    Page<CEPListExamPaperResponse> getListExamPaper(Pageable pageable, CEPListExamPaperRequest request, String userId, String departmentFacilityId, String semesterId, String examPaperStatus, String examPaperType);

    @Query(value = """ 
                 SELECT
                    DISTINCT
                 	CONCAT(m.name, ' - ', f.name) as majorFacilityName,
                 	mf.id as majorFacilityId
                 FROM
                 	staff_major_facility smf
                 JOIN major_facility mf ON
                    mf.id = smf.id_major_facility
                 JOIN major m ON
                    m.id = mf.id_major
                 JOIN department_facility df ON
                    df.id = mf.id_department_facility
                 JOIN facility f ON
                    f.id = df.id_facility
                 JOIN head_subject_by_semester hsbs ON
                    hsbs.id_staff = smf.id_staff
                 WHERE smf.id_major_facility = :majorFacilityId AND
                       smf.id_staff = :staffId AND
                       hsbs.id_semester = :semesterId AND
                       smf.status = 0
            """, nativeQuery = true)
    List<CEPListMajorFacilityResponse> getMajorFacilityByDepartmentFacilityId(String majorFacilityId, String staffId, String semesterId);

    Optional<ExamPaper> findByPath(String fileId);

    @Query("""
            SELECT ep
            FROM ExamPaper ep
            WHERE ep.id = :examPaperId AND
                  ep.status = 0
            """)
    Optional<ExamPaper> findExamPaperById(String examPaperId);

}
