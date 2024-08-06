package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.entity.MajorFacility;
import fplhn.udpm.examdistribution.repository.MajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigMajorFacilityCustomRepository extends MajorFacilityRepository {

    @Query("""
                    SELECT DISTINCT mf
                    FROM MajorFacility mf
                    WHERE mf.departmentFacility.department.name = :departmentName
                    AND mf.departmentFacility.facility.code = :facilityCode
                    AND mf.major.name = :majorName
            """)
    List<MajorFacility> getMajorFacilities(String departmentName, String majorName, String facilityCode);

    Optional<MajorFacility> findByDepartmentFacility_Facility_Code(String facilityCode);

}
