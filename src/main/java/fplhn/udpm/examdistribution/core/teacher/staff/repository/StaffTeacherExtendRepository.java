package fplhn.udpm.examdistribution.core.teacher.staff.repository;

import fplhn.udpm.examdistribution.core.teacher.staff.model.response.StaffTeacherResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface StaffTeacherExtendRepository extends StaffRepository {

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
    StaffTeacherResponse findFirstSupervisorIdByExamShiftCode(String examShiftCode);

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
    StaffTeacherResponse findSecondSupervisorIdByExamShiftCode(String examShiftCode);

}
