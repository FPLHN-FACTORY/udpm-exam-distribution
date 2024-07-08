package fplhn.udpm.examdistribution.core.headsubject.joinroom.staff.repository;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.staff.response.HSStaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HSStaffExtendRepository extends StaffRepository {

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
    HSStaffResponse findFirstSupervisorIdByExamShiftCode(String examShiftCode);

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
    HSStaffResponse findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
