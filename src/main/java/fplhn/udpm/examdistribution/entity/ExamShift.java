package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
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

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
@Table(name = "exam_shift")
public class ExamShift extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_subject_class")
    private ClassSubject classSubject;

    @ManyToOne
    @JoinColumn(name = "id_first_supervisor")
    private Staff firstSupervisor;

    @ManyToOne
    @JoinColumn(name = "id_second_supervisor")
    private Staff secondSupervisor;

    @Column(name = "exam_shift_code")
    private String examShiftCode;

    @Column(name = "exam_date")
    private Long examDate;

    @Column(name = "shift")
    @Enumerated(EnumType.STRING)
    private Shift shift;

    @Column(name = "room")
    private String room;

//    @Column(name = "hash")
//    private String hash;
//
//    @Column(name = "salt")
//    private String salt;

    @Column(name = "password")
    private String password;

    @Column(name = "exam_shift_status")
    @Enumerated(EnumType.STRING)
    private ExamShiftStatus examShiftStatus;

}
