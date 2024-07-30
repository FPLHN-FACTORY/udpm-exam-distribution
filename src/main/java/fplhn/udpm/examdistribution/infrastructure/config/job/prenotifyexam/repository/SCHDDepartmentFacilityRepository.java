package fplhn.udpm.examdistribution.infrastructure.config.job.prenotifyexam.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.DepartmentFacilityRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SCHDDepartmentFacilityRepository extends DepartmentFacilityRepository {

    @Query("""
            SELECT df.staff
            FROM DepartmentFacility df
            """)
    Optional<Staff> findHeadDepartmentByDepartmentFacilityId(String departmentFacilityId);

}
