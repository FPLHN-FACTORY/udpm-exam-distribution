package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListExamPaperResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListMajorFacilityResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UEPUploadExamPaperExtendRepository extends ExamPaperRepository {

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
             	mf.id AS majorFacilityId
             FROM
                 head_subject_by_semester hsbs
             JOIN subject_group sg ON
                 sg.id = hsbs.id_subject_group
             JOIN exam_paper ep ON
                 ep.id_subject = sg.id_subject
             JOIN major_facility mf ON
             	mf.id = ep.id_major_facility
             JOIN subject subj ON
             	subj.id = ep.id_subject
             JOIN major m ON
             	m.id = mf.id_major
             JOIN staff st ON
             	st.id = ep.id_staff_upload
             JOIN department_facility df ON
             	df.id = mf.id_department_facility
             JOIN facility f ON
             	f.id = df.id_facility
             WHERE ep.exam_paper_status <> :examPaperStatus AND
                   mf.id_department_facility = :departmentFacilityId AND
                   sg.id_staff = :userId AND
                   ep.status = 0 AND
                   hsbs.status = 0 AND
                   (:#{#request.semesterId} IS NULL OR hsbs.id_semester LIKE CONCAT('%', TRIM(:#{#request.semesterId}) ,'%')) AND
                   (:#{#request.blockId} IS NULL OR ep.id_block LIKE CONCAT('%', TRIM(:#{#request.blockId}) ,'%')) AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """,countQuery = """
            SELECT
             	COUNT(hsbs.id)
             FROM
                 head_subject_by_semester hsbs
             JOIN subject_group sg ON
                 sg.id = hsbs.id_subject_group
             JOIN exam_paper ep ON
                 ep.id_subject = sg.id_subject
             JOIN major_facility mf ON
             	mf.id = ep.id_major_facility
             JOIN subject subj ON
             	subj.id = ep.id_subject
             JOIN major m ON
             	m.id = mf.id_major
             JOIN staff st ON
             	st.id = ep.id_staff_upload
             JOIN department_facility df ON
             	df.id = mf.id_department_facility
             JOIN facility f ON
             	f.id = df.id_facility
             WHERE ep.exam_paper_status <> :examPaperStatus AND
                   mf.id_department_facility = :departmentFacilityId AND
                   sg.id_staff = :userId AND
                   ep.status = 0 AND
                   hsbs.status = 0 AND
                   (:#{#request.semesterId} IS NULL OR hsbs.id_semester LIKE CONCAT('%', TRIM(:#{#request.semesterId}) ,'%')) AND
                   (:#{#request.blockId} IS NULL OR ep.id_block LIKE CONCAT('%', TRIM(:#{#request.blockId}) ,'%')) AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """, nativeQuery = true)
    Page<ListExamPaperResponse> getListExamPaper(Pageable pageable, ListExamPaperRequest request, String userId, String departmentFacilityId, String examPaperStatus);

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
    List<ListMajorFacilityResponse> getMajorFacilityByDepartmentFacilityId(String majorFacilityId, String staffId, String semesterId);

    Optional<ExamPaper> findByPath(String fileId);

}
