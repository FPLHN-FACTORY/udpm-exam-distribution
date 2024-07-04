package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.Student;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import fplhn.udpm.examdistribution.repository.StudentRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthStudentRepository extends StudentRepository {

    @Query("""
            SELECT s
            FROM Student s
            WHERE s.email = :email
            """)
    Optional<Student> isStudentExist(String email);

    @Query("""
            SELECT s
            FROM Student s
            WHERE s.email = :email AND s.status = 1
            """)
    Optional<Student> isStudentBan(String email);

}
