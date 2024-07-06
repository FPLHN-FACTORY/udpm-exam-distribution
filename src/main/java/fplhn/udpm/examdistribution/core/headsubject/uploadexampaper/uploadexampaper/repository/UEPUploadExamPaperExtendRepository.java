package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListExamPaperResponse;
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
            """,nativeQuery = true)
    List<ListSubjectResponse> getListSubject(String userId);

    @Query("""
            SELECT ep
            FROM ExamPaper ep
            WHERE ep.status = 0
            """)
    List<ExamPaper> getListExamPaper();

}
