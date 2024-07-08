package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository;

import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityChildExcelCustomRepository extends FacilityChildRepository {

    @Query(value = """
            SELECT DISTINCT fc.name 
            FROM facility_child fc
            WHERE fc.status = 0
            """, nativeQuery = true)
    List<String> findAllDistinct();

    List<FacilityChild> findAllByName(String facilityChildName);

}
