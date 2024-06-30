package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository;

import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.repository.DepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentFacilityCustomRepository extends DepartmentFacilityRepository {

    @Query("""
        select distinct df
                    from DepartmentFacility df
                    join fetch df.department d
                    join fetch df.facility f
                    where d.name = :departmentName and f.name = :facilityName
""")
    List<DepartmentFacility> findByDepartmentFacility(String departmentName,String facilityName);


    @Query(
            value = """
            SELECT CONCAT(d.name,' - ',f.name) AS departmentFacilityName
            FROM department_facility df
            JOIN department d ON d.id = df.id_department
            JOIN facility f ON f.id = df.id_facility
            WHERE df.status = 0
            AND df.id_facility LIKE :idFacility
           """,
            nativeQuery = true
    )
    List<String> findAllByIdFacility(String idFacility);

}
