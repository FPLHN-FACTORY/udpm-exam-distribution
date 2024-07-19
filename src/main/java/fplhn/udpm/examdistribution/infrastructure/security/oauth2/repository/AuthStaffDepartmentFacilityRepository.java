package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthStaffDepartmentFacilityRepository extends JpaRepository<StaffDepartmentFacility, String> {

    @Query("""
            SELECT sdf
            FROM StaffDepartmentFacility sdf
            WHERE sdf.staff.id = :staffId AND
                  sdf.departmentFacility.facility.id = :facilityId AND
                  sdf.status = 0
            """)
    Optional<StaffDepartmentFacility> findByStaffIdAndFacilityId(String staffId, String facilityId);

}
