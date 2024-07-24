package fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.repository;

import fplhn.udpm.examdistribution.entity.HeadSubjectBySemester;
import fplhn.udpm.examdistribution.repository.HeadSubjectBySemesterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SCHeadSubjectBySemesterExtendRepository extends HeadSubjectBySemesterRepository {

    List<HeadSubjectBySemester> findAllBySemester_Id(String semesterId);

}
