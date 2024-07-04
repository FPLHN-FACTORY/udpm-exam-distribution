package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "assign_staff_subject")
public class AssignStaffSubject extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id")
    private Staff staff;

    @ManyToOne
    @Column(name = "id_subject")
    private Subject subject;

}
