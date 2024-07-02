package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthStaffRepository extends StaffRepository {

    Optional<Staff> getStaffByAccountFpt(String emailFPT);

    @Query("""
            SELECT r.name
            FROM Staff s
            JOIN StaffRole sr ON s.id = sr.staff.id
            JOIN Role r ON r.id = sr.role.id
            """)
    List<String> getListRoleByEmail(String email);

}
