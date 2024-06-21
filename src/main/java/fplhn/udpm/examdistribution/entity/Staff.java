package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "staff")
public class Staff extends PrimaryEntity implements Serializable {

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String name;

    @Column(name = "staff_code", length = EntityProperties.LENGTH_NAME)
    private String staffCode;

    @Column(name = "account_fe", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String accountFe;

    @Column(name = "account_fpt", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String accountFpt;

    @ManyToOne
    @JoinColumn(name = "id_department_facility")
    private DepartmentFacility departmentFacility;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_recently_semester")
    @ToString.Exclude
    private Semester semester;

}
