package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.repository.StaffMajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthStaffMajorFacilityRepository extends StaffMajorFacilityRepository {

    @Query("""
            SELECT smf
            FROM StaffMajorFacility smf
            WHERE smf.staff.id = :staffId AND
                  smf.majorFacility.departmentFacility.facility.id = :facilityId AND
                  smf.status = 0
            """)
    List<StaffMajorFacility> findByStaffIdAndFacilityId(String staffId, String facilityId);

}
