package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffMajorFacilityDetailResponse;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.repository.StaffMajorFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HOStaffMajorFacilityStaffRepository extends StaffMajorFacilityRepository {

    @Query(value = """
            SELECT smf
            FROM StaffMajorFacility smf
            WHERE smf.majorFacility.departmentFacility.facility.id LIKE :idFacility
            AND smf.staff.id = :idStaff
    """)
    List<StaffMajorFacility> checkStaffMajorFacilityExists(String idFacility, String idStaff);

    @Query(value = """
            SELECT smf.id AS staffMajorFacilityId,
            df.id_facility AS facilityId,
            df.id_department AS departmentId,
            mf.id_major AS majorId
            FROM staff_major_facility smf
            JOIN major_facility mf ON mf.id = smf.id_major_facility
            JOIN department_facility df ON df.id = mf.id_department_facility
            WHERE smf.id LIKE :idStaffMajorFacility
            AND smf.status = 0
            """,nativeQuery = true)
    List<HOStaffMajorFacilityDetailResponse> detailStaffMajorFacility(String idStaffMajorFacility);

}
