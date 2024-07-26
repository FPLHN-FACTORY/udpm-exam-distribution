package fplhn.udpm.examdistribution.core.headoffice.staff.repository;

import fplhn.udpm.examdistribution.core.headoffice.staff.model.request.HOStaffRoleRequest;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffRoleCheckResponse;
import fplhn.udpm.examdistribution.core.headoffice.staff.model.response.HOStaffRoleResponse;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRoleRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HOStaffRoleRepository extends StaffRoleRepository {

    @Query(value = """
            SELECT r.name AS roleName,
                   r.name AS roleCode,
                   r.id AS idRole,
                   f.name AS facilityName
            FROM staff_role sr
            LEFT JOIN role r ON sr.id_role = r.id
            LEFT JOIN facility f ON r.id_facility = f.id
            WHERE sr.status = 0
            AND sr.id_staff = :staffId
            """,
            countQuery = """
                    SELECT COUNT(*)
                    FROM staff_role sr
                    LEFT JOIN role r ON sr.id_role = r.id
                    LEFT JOIN facility f ON r.id_facility = f.id
                    WHERE sr.status = 0
                    AND sr.id_staff = :staffId
                    """,
            nativeQuery = true)
    List<HOStaffRoleResponse> getRolesByStaffId(String staffId);

    @Query(value = """ 
            SELECT r.name AS roleName,
                   r.id AS idRole,
                   r.code AS roleCode,
                   f.name AS facilityName,
                   CASE
                   WHEN r.id IN (SELECT sr.id_role FROM exam_distribution.staff_role sr 
                                                   WHERE sr.id_staff LIKE :#{#hoRoleRequest.staffId}
                                                   AND sr.status = 0)
                   THEN 'true'
                   ELSE 'false'
                   END as checked
                  FROM exam_distribution.role r
                  LEFT JOIN exam_distribution.facility f ON r.id_facility = f.id
                  WHERE r.status = 0
                  AND (:#{#hoRoleRequest.roleName} IS NULL 
                           OR :#{#hoRoleRequest.roleName} LIKE '' 
                           OR r.name LIKE %:#{#hoRoleRequest.roleName}%
                           OR r.code LIKE %:#{#hoRoleRequest.roleName}%)
                  AND (:#{#hoRoleRequest.idFacility} IS NULL 
                           OR :#{#hoRoleRequest.idFacility} LIKE '' 
                           OR r.id_facility LIKE :#{#hoRoleRequest.idFacility})
                  ORDER BY r.last_modified_date desc
              """, countQuery = """
                      SELECT COUNT(*)
                      FROM exam_distribution.role r
                      LEFT JOIN exam_distribution.facility f ON r.id_facility = f.id
                      WHERE r.status = 0
                      AND (:#{#hoRoleRequest.roleName} IS NULL 
                               OR :#{#hoRoleRequest.roleName} LIKE '' 
                               OR r.name LIKE %:#{#hoRoleRequest.roleName}%
                               OR r.code LIKE %:#{#hoRoleRequest.roleName}%)
                      AND (:#{#hoRoleRequest.idFacility} IS NULL 
                               OR :#{#hoRoleRequest.idFacility} LIKE '' 
                               OR r.id_facility LIKE :#{#hoRoleRequest.idFacility})
                      ORDER BY r.last_modified_date desc
            """, nativeQuery = true)
    Page<HOStaffRoleCheckResponse> getRolesChecked(Pageable pageable, HOStaffRoleRequest hoRoleRequest);

    List<StaffRole> findAllByRole_IdAndStaff_Id(String roleId, String staffId);

    List<StaffRole> findAllByStaff_IdAndStatus(String staffId, EntityStatus status);

    List<StaffRole> findAllByStaff_Id(String staffId);

}
