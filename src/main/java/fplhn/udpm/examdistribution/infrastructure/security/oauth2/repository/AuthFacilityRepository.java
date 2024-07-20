package fplhn.udpm.examdistribution.infrastructure.security.oauth2.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthFacilityRepository extends FacilityRepository {

    @Query("""
            SELECT f
            FROM Facility f
            WHERE f.id = :id AND
                  f.status = 0
            """)
    Optional<Facility> findFacilityById(String id);

}
