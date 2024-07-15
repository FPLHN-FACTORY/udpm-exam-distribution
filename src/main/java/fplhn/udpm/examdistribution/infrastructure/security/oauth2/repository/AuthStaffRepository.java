package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthStaffRepository extends StaffRepository {

    @Query(value = """
            SELECT s.*
            FROM staff s
            JOIN staff_role sr ON s.id = sr.id_staff
            JOIN role r ON r.id = sr.id_role
            JOIN facility f ON r.id_facility = f.id
            WHERE s.account_fpt = :emailFPT AND
                  f.id = :facilityId AND
                  r.name = :role AND
                  sr.status = 0
            LIMIT 1
            """, nativeQuery = true)
    Optional<Staff> getStaffByAccountFptAndFacilityId(String emailFPT, String facilityId, String role);

    Optional<Staff> getStaffByAccountFpt(String emailFPT);

}
