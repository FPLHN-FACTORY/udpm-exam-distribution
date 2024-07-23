package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.repository.StaffMajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigStaffMajorFacilityRepository extends StaffMajorFacilityRepository {

@Query(value = """
    SELECT smf
    FROM StaffMajorFacility smf
    WHERE smf.staff.id = :staffId
    AND smf.majorFacility.departmentFacility.facility.code = :facilityCode
    """)
    List<StaffMajorFacility> findAllByStaffAndFacility(String staffId, String facilityCode);

}
