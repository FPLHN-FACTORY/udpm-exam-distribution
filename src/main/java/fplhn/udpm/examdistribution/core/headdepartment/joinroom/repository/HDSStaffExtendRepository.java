package fplhn.udpm.examdistribution.core.headdepartment.joinroom.repository;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.response.HDStaffResponse;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDSStaffExtendRepository extends StaffRepository {

    Optional<Staff> findByStaffCode(String staffCode);

    @Query(value = """
            SELECT
            	s.id,
            	s.staff_code,
            	s.name,
            	s.account_fe,
            	s.account_fpt,
            	s.picture,
            	s.status,
            	s.created_date,
            	s.last_modified_date
            FROM
            	staff s
            JOIN staff_major_facility smf ON
            	s.id = smf.id_staff
            JOIN major_facility mf ON
            	smf.id_major_facility = mf.id
            JOIN department_facility df ON
            	mf.id_department_facility = df.id
            WHERE
                s.staff_code = :staffCode
                AND df.id = :departmentFacilityId
            """, nativeQuery = true)
    Optional<Staff> findByStaffCodeAndDepartmentFacilityId(String staffCode, String departmentFacilityId);

    @Query(value = """
            SELECT
            	s.id as id,
            	s.name as name,
            	s.staff_code as staffCode,
            	s.account_fe as accountFe,
            	s.account_fpt as accountFpt
            FROM
            	staff s
            JOIN exam_shift es ON
            	s.id = es.id_first_supervisor
            WHERE
            	es.exam_shift_code = :examShiftCode
                        """, nativeQuery = true)
    HDStaffResponse findFirstSupervisorIdByExamShiftCode(String examShiftCode);

    @Query(value = """
            SELECT
            	s.id as id,
            	s.name as name,
            	s.staff_code as staffCode,
            	s.account_fe as accountFe,
            	s.account_fpt as accountFpt
            FROM
            	staff s
            JOIN exam_shift es ON
            	s.id = es.id_second_supervisor
            WHERE
            	es.exam_shift_code = :examShiftCode
                        """, nativeQuery = true)
    HDStaffResponse findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
