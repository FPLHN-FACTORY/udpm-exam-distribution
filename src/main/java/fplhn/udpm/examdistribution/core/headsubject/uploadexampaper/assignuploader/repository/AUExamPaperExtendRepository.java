package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.repository;

import fplhn.udpm.examdistribution.entity.ExamPaper;
import fplhn.udpm.examdistribution.repository.ExamPaperRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AUExamPaperExtendRepository extends ExamPaperRepository {

    @Query("""
            SELECT ep
            FROM ExamPaper ep
            WHERE ep.subject.id = :subjectId AND
                  ep.block.semester.id = :semesterId AND
                  ep.examPaperType = "SAMPLE_EXAM_PAPER"
            """)
    Optional<ExamPaper> findSampleExamPaperBySubjectId(String subjectId, String semesterId);


    Optional<ExamPaper> findByPath(String fileId);

}
