package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffCustomRepository extends StaffRepository {

    List<Staff> findByStaffCode(String id);

}
