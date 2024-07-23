package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.repository.DepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfigDepartmentFacilityCustomRepository extends DepartmentFacilityRepository {

    @Query(
            value = """
            SELECT CONCAT(d.name,' - ',m.name,' - ',f.code) AS majorFacilityName
            FROM major_facility mf
            JOIN department_facility df ON df.id = mf.id_department_facility
            JOIN major m ON m.id = mf.id_major
            JOIN department d ON d.id = df.id_department
            JOIN facility f ON f.id = df.id_facility
            WHERE df.status = 0
            AND mf.status = 0                  
            AND df.id_facility LIKE :idFacility
           """,
            nativeQuery = true
    )
    List<String> findAllByIdFacility(String idFacility);

}
