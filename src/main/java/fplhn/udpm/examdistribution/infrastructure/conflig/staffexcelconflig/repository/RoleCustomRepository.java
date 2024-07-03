package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository;

import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.repository.RoleRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleCustomRepository extends RoleRepository {

    @Query(
            value = """
           SELECT CONCAT(r.name,' - ',f.name) AS roleName
           FROM role r
           LEFT JOIN facility f ON f.id = r.id_facility
           WHERE r.status = 0
           AND r.id_facility LIKE :idFacility
           """,
            nativeQuery = true
    )
    List<String> findAllByFacilityId(String idFacility);

    @Query(
            value = """
            select distinct r
            from Role r
            join fetch r.facility f
            where r.name = :roleName and f.name = :facilityName
           """)
    List<Role> findAllByRoleNameAndFacilityName(String roleName, String facilityName);

}
