package fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfigStaffCustomRepository extends StaffRepository {

    Optional<Staff> findByStaffCodeAndStatus(String code, EntityStatus status);

    Optional<Staff> findByAccountFe(String accountFe);

    Optional<Staff> findByAccountFpt(String accountFpt);

}
