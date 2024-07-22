package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.ClassSubject;
import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPExamShiftRepository extends ExamShiftRepository {

    Boolean existsByClassSubjectAndExamDateAndShift(ClassSubject classSubject, Long examDate, Shift shift);

    Optional<ExamShift> findByClassSubjectAndExamDateAndShift(ClassSubject classSubject, Long examDate, Shift shift);

}
