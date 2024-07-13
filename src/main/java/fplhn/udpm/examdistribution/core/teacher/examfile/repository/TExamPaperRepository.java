package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TExamPaperRepository extends ExamPaperRepository {

    @Query(value = """
            SELECT COUNT(ep.id) FROM exam_paper ep
            JOIN subject s2 ON s2.id = ep.id_subject
            JOIN assign_uploader au ON s2.id = au.id_subject
            WHERE au.id_staff LIKE '6569526f-f8df-4c58-a725-ba06cba33e65'
            AND s2.id = '50ead123-c2bc-476d-b67e-3b715f5b6047'
            """,nativeQuery = true)
    Integer getCountUploaded(String staffId, String subjectId);

}
