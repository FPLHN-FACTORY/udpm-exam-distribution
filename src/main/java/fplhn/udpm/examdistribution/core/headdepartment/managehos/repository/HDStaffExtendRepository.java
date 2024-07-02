package fplhn.udpm.examdistribution.core.headdepartment.managehos.repository;

import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.request.StaffRequest;
import fplhn.udpm.examdistribution.core.headdepartment.managehos.model.response.StaffResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface HDStaffExtendRepository extends StaffRepository {

    @Query(
            value = """
                    SELECT
                        ROW_NUMBER() OVER (ORDER BY s.id) AS orderNumber,
                        s.id AS id,
                        s.staff_code AS staffCode,
                        s.name AS staffName,
                        s.account_fpt AS accountFPT,
                        s.account_fe AS accountFE
                    FROM
                        staff s
                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.staffName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
                        AND (:#{#request.staffCode} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
                    """,
            countQuery = """
                    SELECT
                        COUNT(s.id)
                    FROM
                        staff s
                    LEFT JOIN department_facility df ON s.id_department_facility = df.id
                    WHERE
                        df.id = :#{#request.departmentFacilityId}
                        AND (:#{#request.staffName} IS NULL OR s.name LIKE CONCAT('%', :#{#request.staffName}, '%'))
                        AND (:#{#request.staffCode} IS NULL OR s.staff_code LIKE CONCAT('%', :#{#request.staffCode}, '%'))
                    """,
            nativeQuery = true
    )
    Page<StaffResponse> getAllStaffs(Pageable pageable, StaffRequest request);

}
