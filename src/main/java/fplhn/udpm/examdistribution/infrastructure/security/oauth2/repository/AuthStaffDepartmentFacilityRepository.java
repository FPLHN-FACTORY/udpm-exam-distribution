package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.StaffDepartmentFacility;
import fplhn.udpm.examdistribution.repository.StaffDepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthStaffDepartmentFacilityRepository extends StaffDepartmentFacilityRepository {

    @Query("""
            SELECT sdf
            FROM StaffDepartmentFacility sdf
            WHERE sdf.staff.id = :staffId AND
                  sdf.departmentFacility.facility.id = :facilityId AND
                  sdf.status = 0
            """)
    Optional<StaffDepartmentFacility> findByStaffIdAndFacilityId(String staffId, String facilityId);

}
