package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository;

import fplhn.udpm.examdistribution.entity.ExamPaperBySemester;
import fplhn.udpm.examdistribution.repository.ExamPaperBySemesterRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CEPChooseExamPaperRepository extends ExamPaperBySemesterRepository {

    @Query("""
            SELECT eps
            FROM ExamPaperBySemester eps
            WHERE eps.examPaper.id = :examPaperId AND
                  eps.semester.id = :semesterId AND
                  eps.examPaper.majorFacility.id = :majorFacilityId AND
                  eps.status = 0
            """)
    Optional<ExamPaperBySemester> findByExamPaperIdAndSemesterId(String examPaperId, String semesterId, String majorFacilityId);

}
