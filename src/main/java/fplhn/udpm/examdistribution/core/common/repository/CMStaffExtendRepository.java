package fplhn.udpm.examdistribution.core.common.repository;

import fplhn.udpm.examdistribution.core.common.model.request.StaffSearchRequest;
import fplhn.udpm.examdistribution.core.common.model.response.StaffSearchResponse;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CMStaffExtendRepository extends StaffRepository {

    @Query(
            value = """
                    SELECT
                        s.id as id,
                        CONCAT(s.staff_code, ' - ', s.name) as staffInfo
                    FROM
                        staff s
                    LEFT JOIN staff_major_facility smf ON s.id = smf.id_staff
                    LEFT JOIN major_facility mf ON mf.id = smf.id_major_facility
                    LEFT JOIN department_facility df ON mf.id_department_facility = df.id
                    WHERE
                        s.id != :#{#request.currentUserId}
                        AND df.id = :#{#request.currentDepartmentFacilityId}
                        AND (:#{#request.q} IS NULL OR (s.staff_code LIKE CONCAT('%',:#{#request.q},'%') OR s.name LIKE CONCAT('%',:#{#request.q},'%')))
                    GROUP BY s.id, s.staff_code, s.name, s.account_fpt, s.account_fe
                    LIMIT 5
                    """,
            nativeQuery = true
    )
    List<StaffSearchResponse> getStaffs(StaffSearchRequest request);

}
