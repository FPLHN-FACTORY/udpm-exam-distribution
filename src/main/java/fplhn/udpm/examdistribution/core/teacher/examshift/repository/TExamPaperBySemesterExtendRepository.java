package fplhn.udpm.examdistribution.core.teacher.examshift.repository;

import fplhn.udpm.examdistribution.repository.ExamPaperBySemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TExamPaperBySemesterExtendRepository extends ExamPaperBySemesterRepository {

    @Query(value = """
            SELECT
            	ep.id
            FROM
            	exam_paper_by_semester epbs
            JOIN exam_paper ep ON
            	epbs.id_exam_paper = ep.id
            JOIN subject s ON
            	ep.id_subject = s.id
            JOIN department d ON
            	d.id = s.id_department
            JOIN department_facility df ON
            	d.id = df.id_department
            WHERE
            	df.id = :departmentFacilityId
            	AND ep.id_subject = :subjectId
            	AND ep.exam_paper_type = 'OFFICIAL_EXAM_PAPER'
            """, nativeQuery = true)
    List<String> getListIdExamPaper(String departmentFacilityId, String subjectId);

}
