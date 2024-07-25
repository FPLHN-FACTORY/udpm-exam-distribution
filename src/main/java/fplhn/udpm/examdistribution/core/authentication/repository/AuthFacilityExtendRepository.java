package fplhn.udpm.examdistribution.core.authentication.repository;

import fplhn.udpm.examdistribution.core.authentication.model.response.ListFacilityResponse;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthFacilityExtendRepository extends FacilityRepository {

    @Query(value = """
            SELECT  f.id AS id,
                    f.name AS name
            FROM facility f
            WHERE f.status = 0
            """,nativeQuery = true)
    List<ListFacilityResponse> getListFacility();

}
