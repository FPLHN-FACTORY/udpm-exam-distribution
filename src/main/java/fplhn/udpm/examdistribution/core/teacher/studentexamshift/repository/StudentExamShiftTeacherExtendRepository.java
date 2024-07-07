package fplhn.udpm.examdistribution.core.teacher.studentexamshift.repository;

import fplhn.udpm.examdistribution.entity.StudentExamShift;
import fplhn.udpm.examdistribution.repository.StudentExamShiftRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentExamShiftTeacherExtendRepository extends StudentExamShiftRepository {

    Optional<StudentExamShift> findByExamShiftIdAndStudentId(String id, String id1);
}
