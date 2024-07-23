package fplhn.udpm.examdistribution.infrastructure.config.job.staff.model.dto;

import fplhn.udpm.examdistribution.entity.Role;
import fplhn.udpm.examdistribution.entity.Staff;
import fplhn.udpm.examdistribution.entity.StaffMajorFacility;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TranferStaffRole {

    private Staff staff;

    private Role role;

    private StaffMajorFacility staffMajorFacility;

}
