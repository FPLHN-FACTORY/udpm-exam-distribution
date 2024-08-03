package fplhn.udpm.examdistribution.core.student.practiceroom.repository;

import fplhn.udpm.examdistribution.core.student.practiceroom.model.response.SPRMockExamPaperResponse;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SPRExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT ep.path AS path, ep.id AS id
            FROM exam_paper ep
            JOIN staff s ON s.id = ep.id_staff_upload
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN major_facility mf ON mf.id = ep.id_major_facility
            JOIN department_facility df ON df.id = mf.id_department_facility
            WHERE ep.exam_paper_type LIKE 'MOCK_EXAM_PAPER'
            AND ep.is_public = 1
            AND s2.id = :idSubject
            AND ep.id_block = :idBlock
            AND df.id_facility = :idFacility
            """, nativeQuery = true)
    List<SPRMockExamPaperResponse> getMockExamPapers(String idSubject, String idFacility,String idBlock);

}
