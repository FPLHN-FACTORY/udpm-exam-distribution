package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffDetailResponse;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffResonpse;
import fplhn.udpm.examdistribution.entity.Staff;
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
                            COALESCE(CONCAT(d.name, ' - ', f.name),'Không có bộ môn') AS departmentFacilityName
                    FROM staff s
                    LEFT JOIN department_facility df on df.id = s.id_department_facility
                    LEFT JOIN department d ON d.id = df.id_department
                    LEFT JOIN facility f ON f.id = df.id_facility
                    WHERE s.status = 0
                    AND (:#{#req.q} IS NULL OR s.name LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.staff_code LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.account_fe LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.account_fpt LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.departmentFacilityId} IS NULL OR df.id = :#{#req.departmentFacilityId})
                    """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM staff s
                    LEFT JOIN department_facility df on df.id = s.id_department_facility
                    LEFT JOIN department d ON d.id = df.id_department
                    LEFT JOIN facility f ON f.id = df.id_facility
                    WHERE s.status = 0
                    AND (:#{#req.q} IS NULL OR s.name LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.staff_code LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.account_fe LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.q} IS NULL OR s.account_fpt LIKE CONCAT('%',:#{#req.q},'%'))
                    AND (:#{#req.departmentFacilityId} IS NULL OR df.id = :#{#req.departmentFacilityId})
                    """,
            nativeQuery = true)
    Page<HOStaffResonpse> getStaffs(Pageable pageable, HOStaffRequest req);

    @Query(value = """
            SELECT  s.id AS id,
                    s.name AS name,
                    s.staff_code AS staffCode,
                    s.account_fe AS accountFE,
                    s.account_fpt AS accountFpt,
                    df.id AS departmentFacilityId,
                    COALESCE(CONCAT(d.name, ' - ', f.name),'Không có bộ môn') AS departmentFacilityName
            FROM staff s
            LEFT JOIN department_facility df ON df.id = s.id_department_facility
            LEFT JOIN department d ON d.id = df.id_department
            LEFT JOIN facility f ON f.id = df.id_facility
            WHERE s.id LIKE :id
            """, nativeQuery = true)
    Optional<HOStaffDetailResponse> getStaff(String id);

    List<Staff> findByStaffCode(String staffCode);

}
