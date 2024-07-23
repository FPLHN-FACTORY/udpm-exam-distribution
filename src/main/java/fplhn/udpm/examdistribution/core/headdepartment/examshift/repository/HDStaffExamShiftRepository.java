package fplhn.udpm.examdistribution.core.headdepartment.examshift.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface HDStaffExamShiftRepository extends StaffRepository {

    Optional<Staff> findByStaffCode(String staffCode);

}
