package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request.ListExamPaperRequest;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListExamPaperResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UEPUploadExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT
             	ep.id AS id,
             	ROW_NUMBER() OVER(ORDER BY ep.id DESC) AS orderNumber,
             	ep.path AS fileId,
             	ep.exam_paper_code AS examPaperCode,
             	ep.id AS examPaperId,
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
             	    SELECT COUNT(seps.id_exam_paper)
             	    FROM exam_paper_shift seps
             	    JOIN exam_shift ses ON ses.id = seps.id_exam_shift
             	    JOIN class_subject scs ON scs.id = ses.id_subject_class
             	    JOIN block sb ON sb.id = scs.id_block
             	    WHERE
             	        seps.id_exam_paper = ep.id AND
             	        sb.id_semester = :semesterId AND
             	        seps.status = 0
             	) AS totalUsed
            FROM
                exam_paper_by_semester epbs
            JOIN exam_paper ep ON
                 ep.id = epbs.id_exam_paper
            JOIN subject subj ON
             	subj.id = ep.id_subject
            JOIN head_subject_by_semester hsbs ON
                hsbs.id_subject = subj.id
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
                   mf.id_department_facility = :departmentFacilityId AND
                   hsbs.id_staff = :userId AND
                   epbs.id_semester = :semesterId AND
                   ep.status = 0 AND
                   epbs.status = 0 AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """, countQuery = """
            SELECT
             	COUNT(epbs.id)
            FROM
                exam_paper_by_semester epbs
            JOIN exam_paper ep ON
                 ep.id = epbs.id_exam_paper
            JOIN subject subj ON
             	subj.id = ep.id_subject
            JOIN head_subject_by_semester hsbs ON
                hsbs.id_subject = subj.id
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
                   mf.id_department_facility = :departmentFacilityId AND
                   hsbs.id_staff = :userId AND
                   epbs.id_semester = :semesterId AND
                   ep.status = 0 AND
                   epbs.status = 0 AND
                   (:#{#request.subjectId} IS NULL OR subj.id LIKE CONCAT('%', TRIM(:#{#request.subjectId}) ,'%')) AND
                   (:#{#request.staffId} IS NULL OR ep.id_staff_upload LIKE CONCAT('%', TRIM(:#{#request.staffId}) ,'%')) AND
                   (:#{#request.examPaperType} IS NULL OR ep.exam_paper_type LIKE CONCAT('%', TRIM(:#{#request.examPaperType}) ,'%'))
            """, nativeQuery = true)
    Page<ListExamPaperResponse> getListExamPaper(
            Pageable pageable,
            ListExamPaperRequest request,
            String userId,
            String departmentFacilityId,
            String semesterId,
            String examPaperStatus
    );

    Optional<ExamPaper> findByPath(String fileId);

}
