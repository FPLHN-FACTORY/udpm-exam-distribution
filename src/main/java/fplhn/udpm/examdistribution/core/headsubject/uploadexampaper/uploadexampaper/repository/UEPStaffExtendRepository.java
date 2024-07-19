package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListStaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPStaffExtendRepository extends StaffRepository {

    @Query(value = """
            SELECT
            	st.id AS id,
            	CONCAT(st.name, "-", st.staff_code) AS name
            FROM staff_department_facility sdf
            JOIN staff st ON st.id = sdf.id_staff
            WHERE
                sdf.id_department_facility = :departmentFacilityId AND
                sdf.status = 0
            """,countQuery = """
            SELECT
            	COUNT(sdf.id)
            FROM staff_department_facility sdf
            JOIN staff st ON st.id = sdf.id_staff
            WHERE
                sdf.id_department_facility = :departmentFacilityId AND
                sdf.status = 0
            """, nativeQuery = true)
    List<ListStaffResponse> getListStaff(String departmentFacilityId);

}
