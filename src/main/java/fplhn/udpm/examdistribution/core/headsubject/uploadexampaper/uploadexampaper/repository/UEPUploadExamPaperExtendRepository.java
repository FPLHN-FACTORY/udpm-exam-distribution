package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListMajorFacilityResponse;
import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListSubjectResponse;
import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
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

    @Query("""
            SELECT ep
            FROM ExamPaper ep
            JOIN Subject subj ON ep.subject.id = subj.id
            JOIN Staff st ON st.id = subj.headSubject.id
            WHERE ep.status = 0 AND
                  ep.examPaperStatus = "APPROVED" AND
                  st.id = :userId AND
                  (:subjectId IS NULL OR subj.id LIKE CONCAT("%",:subjectId,"%"))
            """)
    List<ExamPaper> getListExamPaper(String userId, String subjectId);

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
                 JOIN staff st ON
                 	st.id_department_facility = df.id
                 WHERE mf.status = 0 AND f.id = :facilityId AND df.id_department = :departmentId
            """,nativeQuery = true)
    List<ListMajorFacilityResponse> getMajorFacilityByDepartmentFacilityId(String facilityId, String departmentId);

}
