package fplhn.udpm.examdistribution.core.headoffice.classsubject.repository;

import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.repository.StaffRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffClassSubjectRepository extends StaffRepository {

    Optional<Staff> findByStaffCodeAndStatus(String staffCode, EntityStatus status);

}
