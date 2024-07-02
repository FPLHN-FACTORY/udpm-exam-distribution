package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.repository.DepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthDepartmentFacilityRepository extends DepartmentFacilityRepository {

    @Query("""
            SELECT df
            FROM DepartmentFacility df
            WHERE df.facility.id = :facilityId
            """)
    Optional<DepartmentFacility> getDepartmentFacilityByFacilityId(String facilityId);

}
