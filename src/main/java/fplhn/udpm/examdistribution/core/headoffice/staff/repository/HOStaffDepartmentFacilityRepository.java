package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffDepartmentFacilityResponse;
import fplhn.udpm.examdistribution.entity.DepartmentFacility;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.DepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HOStaffDepartmentFacilityRepository extends DepartmentFacilityRepository {

    @Query(value = """
        SELECT CONCAT(d.name,' - ',f.name) AS departmentFacilityName,
        	   df.id AS departmentFacilityId
        FROM department_facility df
        JOIN department d ON d.id = df.id_department
        JOIN facility f ON f.id = df.id_facility
        WHERE df.status = 0
    """,nativeQuery = true)
    List<HOStaffDepartmentFacilityResponse> getDepartmentFacilities();

    List<DepartmentFacility> findAllByDepartment_IdAndFacility_IdAndStatus(String departmentId, String facilityId, EntityStatus status);

}
