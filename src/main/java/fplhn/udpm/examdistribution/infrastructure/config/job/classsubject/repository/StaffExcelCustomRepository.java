package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StaffExcelCustomRepository extends StaffRepository {

    List<Staff> findAllByStaffCodeAndStatus(String code, EntityStatus status);

}
