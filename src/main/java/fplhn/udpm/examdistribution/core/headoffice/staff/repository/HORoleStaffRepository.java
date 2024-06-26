package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffResonpse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HORoleStaffRepository extends StaffRepository {

    @Query(value = """
            SELECT s.id as id,
            		s.name as name,
            		s.staff_code as staffCode,
            		s.account_fe as accountFE,
            		s.account_fpt as accountFpt,
            		smt.name as recentlySemesterName
            FROM staff_role sr
            LEFT JOIN staff s ON s.id = sr.id_staff
            LEFT JOIN semester smt on smt.id = s.id_recently_semester
            WHERE sr.id_role like :roleId
            
            """,nativeQuery = true)
    Page<HOStaffResonpse> getStaffByRole(Pageable pageable, String roleId);

}
