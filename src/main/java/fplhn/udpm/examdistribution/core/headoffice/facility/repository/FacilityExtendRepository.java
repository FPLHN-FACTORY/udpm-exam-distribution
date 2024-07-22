package fplhn.udpm.examdistribution.core.headoffice.facility.repository;

import fplhn.udpm.examdistribution.core.headoffice.facility.model.request.FacilityRequest;
import fplhn.udpm.examdistribution.core.headoffice.facility.model.response.FacilityResponse;
import fplhn.udpm.examdistribution.entity.Facility;
import fplhn.udpm.examdistribution.repository.FacilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FacilityExtendRepository extends FacilityRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (
                        ORDER BY fa.id DESC ) AS orderNumber,
                        fa.id AS id,
                        fa.name AS facilityName,
                        fa.code AS facilityCode,
                        fa.status AS facilityStatus,
                        fa.created_date AS createdDate
                    FROM
                        facility fa
                    WHERE
                        (:#{#request.name} IS NULL OR fa.name LIKE CONCAT('%',:#{#request.name},'%') OR fa.code LIKE CONCAT('%',:#{#request.name},'%'))
                        AND (:#{#request.status} IS NULL OR fa.status = :#{#request.status}) 
                    """,
            countQuery = """
                    SELECT
                        COUNT(DISTINCT fa.id)
                    FROM
                        facility fa
                    WHERE
                         (:#{#request.name} IS NULL OR fa.name LIKE CONCAT('%',:#{#request.name},'%'))
                        AND (:#{#request.status} IS NULL OR fa.status = :#{#request.status}) 
                    """,
            nativeQuery = true
    )
    Page<FacilityResponse> getAllFacility(Pageable pageable, FacilityRequest request);

    @Query(
            value = """
                    SELECT
                        fa.id AS id,
                        fa.code AS facilityCode,
                        fa.name AS facilityName,
                        fa.status AS facilityStatus,
                        fa.created_date AS createdDate
                    FROM
                        facility fa
                    WHERE
                        fa.id = :facilityId
                    """,
            nativeQuery = true
    )
    Optional<FacilityResponse> getDetailFacilityById(String facilityId);

    List<Facility> findAllByName(String name);

    boolean existsByNameAndIdNot(String name,String id);
}
