package fplhn.udpm.examdistribution.repository;

import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.infrastructure.projection.SimpleEntityProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FacilityRepository extends JpaRepository<Facility, String> {

    @Query(
            value = """
                    SELECT
                        id,
                        name
                    FROM facility
                    ORDER BY created_date
                    """,
            nativeQuery = true
    )
    List<SimpleEntityProjection> findAllSimpleEntity();

}
