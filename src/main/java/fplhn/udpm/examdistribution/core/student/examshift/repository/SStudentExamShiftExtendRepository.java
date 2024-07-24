package fplhn.udpm.examdistribution.core.student.examshift.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.repository.StudentExamShiftRepository;

import java.util.Optional;

public interface SStudentExamShiftExtendRepository extends StudentExamShiftRepository {

    Optional<StudentExamShift> findByExamShiftIdAndStudentId(String examShiftId, String studentId);

}
