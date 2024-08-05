package fplhn.udpm.examdistribution.infrastructure.config.job.staff.commonio;

import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.global.GlobalVariables;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto.TransferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffMajorFacilityRepository;
import fplhn.udpm.examdistribution.infrastructure.config.job.staff.repository.ConfigStaffRoleCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.CSVManipulationUtils;
import jakarta.transaction.Transactional;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
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

    @Setter(onMethod_ = {@Autowired})
    private CSVManipulationUtils csvManipulationUtils;

    @Setter(onMethod_ = {@Autowired})
    private GlobalVariables globalVariables;

    @Override
    public void write(Chunk<? extends TransferStaffRole> chunk) throws Exception {
        if (chunk != null) {
            int recordNumber = 0;
            for (TransferStaffRole transferStaffRole : chunk) {
                recordNumber++;
                try {
                    // Save staff first
                    Staff staff = transferStaffRole.getStaff();
                    List<Staff> staffs = staffCustomRepository
                            .findAllByStaffCodeAndStatus(
                                    staff.getStaffCode(),
                                    EntityStatus.ACTIVE
                            );
                    if (!staffs.isEmpty()) {
                        staff = staffs.get(0);
                    }
                    Staff savedStaff = staffCustomRepository.save(staff);
                    // Save role second
                    Role role = transferStaffRole.getRole();
                    StaffRole staffRole = new StaffRole();
                    staffRole.setStaff(savedStaff);
                    staffRole.setRole(role);
                    staffRole.setId(UUID.randomUUID().toString());
                    staffRole.setStatus(EntityStatus.ACTIVE);
                    StaffRole savedStaffRole = staffRoleCustomRepository.save(staffRole);
                    // Save staff major facility third
                    StaffMajorFacility staffMajorFacility = transferStaffRole.getStaffMajorFacility();
                    staffMajorFacility.setStaff(savedStaff);
                    StaffMajorFacility saveStaffMajorFacility = staffMajorFacilityRepository.save(staffMajorFacility);
                    log.info(
                            """
                                    Saved staff role: %s
                                    Saved staff major facility: %s
                                    """.formatted(
                                    savedStaffRole,
                                    saveStaffMajorFacility
                            )
                    );
                } catch (Exception e) {
                    e.printStackTrace(System.out);
                    log.error("Error processing record number {}: {}", recordNumber, transferStaffRole, e);
                    // Continue with the next record
                }
            }
        }
    }

}
