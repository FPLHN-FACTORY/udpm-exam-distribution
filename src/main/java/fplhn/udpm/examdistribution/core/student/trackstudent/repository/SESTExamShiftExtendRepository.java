package fplhn.udpm.examdistribution.core.student.trackstudent.repository;

import fplhn.udpm.examdistribution.entity.ExamShift;
import fplhn.udpm.examdistribution.repository.ExamShiftRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SESTExamShiftExtendRepository extends ExamShiftRepository {

    Optional<ExamShift> getExamShiftByExamShiftCode(String room);

}
