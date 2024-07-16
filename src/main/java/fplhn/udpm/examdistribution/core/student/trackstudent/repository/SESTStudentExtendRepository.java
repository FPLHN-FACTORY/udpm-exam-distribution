package fplhn.udpm.examdistribution.core.student.trackstudent.repository;

import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.repository.StudentRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SESTStudentExtendRepository extends StudentRepository {

    Optional<Student> getStudentsByEmail(String email);

}
