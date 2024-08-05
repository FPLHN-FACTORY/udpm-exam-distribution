package fplhn.udpm.examdistribution.core.headoffice.role.repository;

import fplhn.udpm.examdistribution.core.headoffice.role.model.response.HORoleFacilityResponse;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HORoleFacilityRepository extends FacilityRepository {
    @Query(value = """ 
              SELECT  f.id AS idFacility,
                      f.name AS facilityName
              FROM facility f
              WHERE f.status=0
            """, nativeQuery = true)
    List<HORoleFacilityResponse> getFacilities();
}
