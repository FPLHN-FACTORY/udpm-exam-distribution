package fplhn.udpm.examdistribution.core.headoffice.facility.repository;

import fplhn.udpm.examdistribution.core.headoffice.facility.model.response.FacilityChildResponse;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.response.FacilityResponse;
import fplhn.udpm.examdistribution.entity.FacilityChild;
import fplhn.udpm.examdistribution.repository.FacilityChildRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacilityChildExtendRepository extends FacilityChildRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY fac.id DESC ) as orderNumber,
                        fac.id as id,
                        fac.name as facilityChildName,
                        fac.status as facilityChildStatus,
                        fac.created_date as createdDate
                    FROM
                        facility_child fac
                    LEFT JOIN
                         facility fa on fa.id = fac.id_facility
                    WHERE fa.id = :facilityId
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT fac.id)
                    FROM
                        facility_child fac
                    LEFT JOIN
                         facility fa on fa.id = fac.id_facility
                    WHERE fa.id = :facilityId
                    """,
            nativeQuery = true
    )
    Page<FacilityChildResponse> getAllFacilityChild(String facilityId, Pageable pageable);

    Optional<FacilityChild> findByName(String name);

    @Query(
            value = """
                    SELECT
                        fac.id as id,
                        fac.name as facilityChildName,
                        fac.status as facilityChildStatus,
                        fac.created_date as createdDate
                    FROM
                        facility_child fac
                    WHERE
                        fac.id = :facilityChildId
                    """,
            nativeQuery = true
    )
    Optional<FacilityChildResponse> getDetailFacilityChildById(String facilityChildId);
}
