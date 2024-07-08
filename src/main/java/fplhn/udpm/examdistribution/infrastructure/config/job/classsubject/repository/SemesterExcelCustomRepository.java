package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Semester;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SemesterName;
import fplhn.udpm.examdistribution.repository.SemesterRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SemesterExcelCustomRepository extends SemesterRepository {

    List<Semester> findAllBySemesterNameAndYearAndStatus(SemesterName semesterName, Integer year, EntityStatus status);

}
