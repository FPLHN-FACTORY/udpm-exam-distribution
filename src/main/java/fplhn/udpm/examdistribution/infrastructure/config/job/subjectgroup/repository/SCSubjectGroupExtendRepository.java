package fplhn.udpm.examdistribution.infrastructure.config.job.subjectgroup.repository;

import fplhn.udpm.examdistribution.entity.SubjectGroup;
import fplhn.udpm.examdistribution.repository.SubjectGroupRepository;
import jdk.jfr.Registered;

import java.util.List;

@Registered
public interface SCSubjectGroupExtendRepository extends SubjectGroupRepository {

    List<SubjectGroup> findAllBySemester_Id(String semesterId);

}
