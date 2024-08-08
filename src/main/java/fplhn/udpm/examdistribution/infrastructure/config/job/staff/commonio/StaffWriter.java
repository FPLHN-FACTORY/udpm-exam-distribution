package fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio;

import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto.TransferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffMajorFacilityRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffRoleCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@Transactional
public class StaffWriter implements ItemWriter<TransferStaffRole> {

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffCustomRepository staffCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffRoleCustomRepository staffRoleCustomRepository;

    @Setter(onMethod_ = {@Autowired})
    private ConfigStaffMajorFacilityRepository staffMajorFacilityRepository;

    @Override
    public void write(Chunk<? extends TransferStaffRole> chunk) throws Exception {
        int recordNumber = 0;

        for (TransferStaffRole transferStaffRole : chunk) {
            recordNumber++;
            try {
                Staff savedStaff = saveOrUpdateStaff(transferStaffRole.getStaff());
                StaffRole savedStaffRole = saveStaffRole(savedStaff, transferStaffRole.getRole());
                StaffMajorFacility savedStaffMajorFacility = saveStaffMajorFacility(savedStaff, transferStaffRole.getStaffMajorFacility());
                log.info(
                        "Successfully processed record number {}: Saved Staff Role - {}, Saved Staff Major Facility - {}",
                        recordNumber, savedStaffRole, savedStaffMajorFacility
                );
            } catch (Exception e) {
                log.error("Error processing record number {}: {}", recordNumber, transferStaffRole, e);
            }
        }
    }

    private Staff saveOrUpdateStaff(Staff staff) {
        return staffCustomRepository
                .findByStaffCodeAndStatus(staff.getStaffCode(), EntityStatus.ACTIVE)
                .orElseGet(() -> staffCustomRepository.save(staff));
    }

    private StaffRole saveStaffRole(Staff staff, Role role) {
        StaffRole staffRole = new StaffRole();
        staffRole.setStaff(staff);
        staffRole.setRole(role);
        staffRole.setId(UUID.randomUUID().toString());
        staffRole.setStatus(EntityStatus.ACTIVE);
        return staffRoleCustomRepository.save(staffRole);
    }

    private StaffMajorFacility saveStaffMajorFacility(Staff staff, StaffMajorFacility staffMajorFacility) {
        staffMajorFacility.setStaff(staff);
        return staffMajorFacilityRepository.save(staffMajorFacility);
    }
}
