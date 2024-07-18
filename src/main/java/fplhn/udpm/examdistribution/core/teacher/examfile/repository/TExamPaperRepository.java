package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TSampleExamPaperResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT COUNT(ep.id) FROM exam_paper ep
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN assign_uploader au ON s2.id = au.id_subject
            WHERE au.id_staff LIKE :staffId
            AND s2.id = :subjectId
            """, nativeQuery = true)
    Integer getCountUploaded(String staffId, String subjectId);

    @Query(value = """
            SELECT ep.path AS path,
                   ep.id AS id
            FROM exam_paper ep
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN department_facility df ON df.id = mf.id_department_facility
            JOIN subject s ON s.id = ep.id_subject
            WHERE df.id = :departmentFacilityId
            AND ep.exam_paper_type LIKE 'SAMPLE_EXAM_PAPER'
            AND s.id = :subjectId
            """, nativeQuery = true)
    List<TSampleExamPaperResponse> getSampleExamPaper(String departmentFacilityId, String subjectId);

}
