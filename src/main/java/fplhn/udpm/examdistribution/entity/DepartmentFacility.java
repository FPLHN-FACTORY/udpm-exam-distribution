package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DynamicUpdate
@Table(name = "department_facility")
@ToString
public class DepartmentFacility extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;

    @ManyToOne
    @JoinColumn(name = "id_department")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

}
