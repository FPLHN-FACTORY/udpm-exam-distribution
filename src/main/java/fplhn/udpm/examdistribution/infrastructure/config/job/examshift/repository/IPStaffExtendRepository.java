package fplhn.udpm.examdistribution.infrastructure.config.job.examshift.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IPStaffExtendRepository extends StaffRepository {

    Optional<Staff> findByStaffCode(String staffCode);

}
