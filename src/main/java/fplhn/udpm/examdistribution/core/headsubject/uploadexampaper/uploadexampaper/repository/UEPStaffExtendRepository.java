package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.repository;

import fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response.ListStaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UEPStaffExtendRepository extends StaffRepository {

    @Query(value = """
            SELECT DISTINCT
            	st.id AS id,
            	CONCAT(st.name, "-", st.staff_code) AS name
            FROM
            	staff st
            WHERE
            	st.status = 0 AND
            	st.id_department_facility = :departmentFacilityId
            """,countQuery = """
            SELECT
            	COUNT(DISTINCT st.id)
            FROM
            	staff st
            WHERE
            	st.status = 0 AND
            	st.id_department_facility = :departmentFacilityId
            """, nativeQuery = true)
    List<ListStaffResponse> getListStaff(String departmentFacilityId);

}
