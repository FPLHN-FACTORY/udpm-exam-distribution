package fplhn.udpm.examdistribution.core.teacher.examfile.repository;

import fplhn.udpm.examdistribution.core.teacher.examfile.model.response.TMajorFacilityResponse;
import fplhn.udpm.examdistribution.repository.MajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TMajorFacilityRepository extends MajorFacilityRepository {

    @Query(value = """ 
                 SELECT CONCAT(m.name,' - ',f.name) as majorFacilityName,
                       mf.id as majorFacilityId
                 FROM major_facility mf
                 JOIN major m ON m.id = mf.id_major
                 JOIN department_facility df ON df.id = mf.id_department_facility
                 JOIN facility f ON f.id = df.id_facility
                 WHERE mf.status = 0
                 AND df.id = :departmentFacilityId
            """,nativeQuery = true)
    List<TMajorFacilityResponse> getMajorFacilityByIdFacility(String departmentFacilityId);

}
