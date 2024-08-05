package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.EntityProperties;
import fplhn.udpm.examdistribution.infrastructure.constant.SubjectStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.SubjectType;
import fplhn.udpm.examdistribution.infrastructure.listener.SubjectListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Nationalized;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subject")
@EntityListeners(SubjectListener.class)
public class Subject extends PrimaryEntity implements Serializable {

    @Column(name = "name", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String name;

    @Column(name = "subject_code", length = EntityProperties.LENGTH_NAME)
    @Nationalized
    private String subjectCode;

    @Column(name = "subject_type")
    @Enumerated(EnumType.STRING)
    @Nationalized
    private SubjectType subjectType;

    @Column(name = "created_time")
    private Long createdTime;

    @ManyToOne
    @JoinColumn(name = "id_department")
    private Department department;

    @ManyToOne
    @JoinColumn(name = "id_exam_rule")
    private ExamRule examRule;

}
