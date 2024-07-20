package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response.CEPListStaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CEPStaffExtendRepository extends StaffRepository {

    @Query(value = """
            SELECT
            	st.id AS id,
            	CONCAT(st.name, "-", st.staff_code) AS name
            FROM staff_department_facility sdf
            JOIN staff st ON st.id = sdf.id_staff
            WHERE
                sdf.id_department_facility = :departmentFacilityId AND
                sdf.status = 0
            """, countQuery = """
            SELECT
            	COUNT(sdf.id)
            FROM staff_department_facility sdf
            JOIN staff st ON st.id = sdf.id_staff
            WHERE
                sdf.id_department_facility = :departmentFacilityId AND
                sdf.status = 0
            """, nativeQuery = true)
    List<CEPListStaffResponse> getListStaff(String departmentFacilityId);

}
