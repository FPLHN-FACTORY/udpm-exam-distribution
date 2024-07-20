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
            FROM staff_major_facility smf
            JOIN major_facility mf ON mf.id = smf.id_major_facility
            JOIN staff st ON st.id = smf.id_staff
            WHERE
                mf.id_department_facility = :departmentFacilityId AND
                smf.status = 0
            """, countQuery = """
            SELECT
            	COUNT(sdf.id)
            FROM staff_major_facility smf
            JOIN major_facility mf ON mf.id = smf.id_major_facility
            JOIN staff st ON st.id = smf.id_staff
            WHERE
                mf.id_department_facility = :departmentFacilityId AND
                smf.status = 0
            """, nativeQuery = true)
    List<CEPListStaffResponse> getListStaff(String departmentFacilityId);

}
