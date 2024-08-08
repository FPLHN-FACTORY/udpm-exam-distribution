package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityProperties;
import fplhn.udpm.examdistribution.infrastructure.constant.Shift;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
@Table(name = "class_subject")
public class ClassSubject extends PrimaryEntity implements Serializable {

    @Column(name = "class_subject_code", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String classSubjectCode;

    @Column(name = "day")
    private Long day;

    @Enumerated(EnumType.STRING)
    @Column(name = "shift")
    private Shift shift;

    @ManyToOne
    @JoinColumn(name = "id_subject")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "id_block")
    private Block block;

    @ManyToOne
    @JoinColumn(name = "id_facility_child")
    private FacilityChild facilityChild;

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

}
