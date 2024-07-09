package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListExamPaperResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListMajorFacilityResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListSubjectResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPUploadExamPaperExtendRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT  s.id AS id,
                    s.name AS name
            FROM subject s
            WHERE s.id_head_subject = :userId
            """, nativeQuery = true)
    List<ListSubjectResponse> getListSubject(String userId);

    @Query(value = """
            SELECT ep.id AS id,
                   ROW_NUMBER() OVER(
            	   ORDER BY ep.id DESC) AS orderNumber,
                   ep.path AS fileId,
                   ep.exam_paper_code AS examPaperCode,
                   ep.exam_paper_type AS examPaperType,
                   ep.created_exam_paper_date AS createdDate,
                   ep.exam_paper_status AS status,
                   subj.id AS subjectId,
                   subj.name AS subjectName,
                   m.name AS majorName,
                   CONCAT(st.staff_code,"_",st.name) AS staffName,
                   f.name AS facilityName,
                   mf.id AS majorFacilityId
            FROM exam_paper ep
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN subject subj ON subj.id = ep.id_subject
            JOIN major m ON m.id = mf.id_major
            JOIN staff st ON st.id = ep.id_staff_upload
            JOIN department_facility df ON df.id = mf.id_department_facility
            JOIN facility f ON f.id = df.id_facility
            WHERE ep.status = 0 AND
                  subj.id_head_subject = :userId AND
                  ep.exam_paper_status <> :examPaperStatus AND
                  (:subjectId IS NULL OR subj.id LIKE CONCAT("%",:subjectId,"%"))
            """, countQuery = """
            SELECT COUNT(ep.id)
            FROM exam_paper ep
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN subject subj ON subj.id = ep.id_subject
            JOIN major m ON m.id = mf.id_major
            JOIN staff st ON st.id = ep.id_staff_upload
            JOIN department_facility df ON df.id = mf.id_department_facility
            JOIN facility f ON f.id = df.id_facility
            WHERE ep.status = 0 AND
                  subj.id_head_subject = :userId AND
                  ep.exam_paper_status <> :examPaperStatus AND
                  (:subjectId IS NULL OR subj.id LIKE CONCAT("%",:subjectId,"%"))
            """, nativeQuery = true)
    Page<ListExamPaperResponse> getListExamPaper(Pageable pageable, String subjectId, String userId, String examPaperStatus);

    @Query(value = """ 
                 SELECT
                 	CONCAT(m.name, ' - ', f.name) as majorFacilityName,
                 	mf.id as majorFacilityId
                 FROM
                 	major_facility mf
                 JOIN major m ON
                 	m.id = mf.id_major
                 JOIN department_facility df ON
                 	df.id = mf.id_department_facility
                 JOIN facility f ON
                 	f.id = df.id_facility
                 WHERE mf.status = 0 AND f.id = :facilityId AND df.id_department = :departmentId
            """, nativeQuery = true)
    List<ListMajorFacilityResponse> getMajorFacilityByDepartmentFacilityId(String facilityId, String departmentId);

}
