package fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.commonio;

import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffRole;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.model.dto.TranferStaffRole;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository.StaffCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.conflig.staffexcelconflig.repository.StaffRoleCustomRepository;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityStatus;
import fplhn.udpm.examdistribution.utils.CodeGenerator;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@Transactional
public class StaffWriter implements ItemWriter<TranferStaffRole> {

    @Autowired
    private StaffCustomRepository staffCustomRepository;

    @Autowired
    private StaffRoleCustomRepository staffRoleCustomRepository;

    @Override
    public void write(Chunk<? extends TranferStaffRole> chunk) throws Exception {
        if (chunk!=null){
            for (TranferStaffRole tranferStaffRole : chunk) {
                try {
                    Staff staff = tranferStaffRole.getStaff();
                    List<Staff> staffs = staffCustomRepository.findByStaffCode(staff.getStaffCode());
                    if (!staffs.isEmpty()) {
                        staff = staffs.get(0);
                    }
                    Staff savedStaff = staffCustomRepository.save(staff); // Save staff first
                    Role role = tranferStaffRole.getRole();
                    StaffRole staffRole = new StaffRole();
                    staffRole.setStaff(savedStaff);
                    staffRole.setRole(role);
                    staffRole.setId(CodeGenerator.generateRandomCode());
                    staffRole.setStatus(EntityStatus.ACTIVE);
                    StaffRole savedStaffRole = staffRoleCustomRepository.save(staffRole);
                    log.info("Staff: " + savedStaff.toString());
                    log.info("Role: " + savedStaffRole.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("Error processing record: " + tranferStaffRole, e);
                    // Continue with the next record
                }
            }
        }
    }

}
