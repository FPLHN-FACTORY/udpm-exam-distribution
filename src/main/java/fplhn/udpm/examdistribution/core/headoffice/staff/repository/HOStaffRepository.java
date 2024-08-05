package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffDetailResponse;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffResonpse;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HOStaffRepository extends StaffRepository {

    @Query(
            value = """
                     SELECT  s.id AS id,
                             s.name AS name,
                             s.staff_code AS staffCode,
                             s.account_fe AS accountFE,
                             s.account_fpt AS accountFpt,
                             s.status AS status
                     FROM staff s
                     WHERE (:#{#req.searchQuery} IS NULL
                              OR :#{#req.searchQuery} LIKE ''
                              OR s.name LIKE CONCAT('%',:#{#req.searchQuery},'%')
                              OR s.staff_code LIKE CONCAT('%',:#{#req.searchQuery},'%')
                              OR s.account_fe LIKE CONCAT('%',:#{#req.searchQuery},'%')
                              OR s.account_fpt LIKE CONCAT('%',:#{#req.searchQuery},'%'))
                    AND (:#{#req.status} IS NULL OR :#{#req.status} LIKE '' OR s.status = :#{#req.status})
                     ORDER BY s.created_date DESC
                     """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM staff s
                    WHERE (:#{#req.searchQuery} IS NULL
                             OR :#{#req.searchQuery} LIKE ''
                             OR s.name LIKE CONCAT('%',:#{#req.searchQuery},'%')
                             OR s.staff_code LIKE CONCAT('%',:#{#req.searchQuery},'%')
                             OR s.account_fe LIKE CONCAT('%',:#{#req.searchQuery},'%')
                             OR s.account_fpt LIKE CONCAT('%',:#{#req.searchQuery},'%'))
                    AND (:#{#req.status} IS NULL OR :#{#req.status} LIKE '' OR s.status = :#{#req.status})
                    """,
            nativeQuery = true)
    Page<HOStaffResonpse> getStaffs(Pageable pageable, HOStaffRequest req);

    @Query(value = """
            SELECT  s.id AS id,
                    s.name AS name,
                    s.staff_code AS staffCode,
                    s.account_fe AS accountFE,
                    s.account_fpt AS accountFpt
            FROM staff s
            WHERE s.id LIKE :id
            """, nativeQuery = true)
    Optional<HOStaffDetailResponse> getStaff(String id);

    List<Staff> findByStaffCode(String staffCode);

    Optional<Staff> findByIdAndStatus(String id, EntityStatus status);

}
