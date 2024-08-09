package fplhn.udpm.examdistribution.core.headoffice.subject.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SFacilityExtendRepository extends FacilityRepository {

    @Query("""
            SELECT f
            FROM Facility f
            WHERE f.status = 0
            """)
    List<Facility> getListFacility();

}
