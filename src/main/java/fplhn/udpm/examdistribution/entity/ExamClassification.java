package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityProperties;
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
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "exam_classification")
public class ExamClassification extends PrimaryEntity implements Serializable {

    @Nationalized
    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    private String name;

    @Column(name = "file_id")
    private String fileId;

    @ManyToOne
    @JoinColumn(name = "department_facility_id")
    private DepartmentFacility departmentFacility;

    @ManyToOne
    @JoinColumn(name = "id_semester")
    private Semester semester;

}
