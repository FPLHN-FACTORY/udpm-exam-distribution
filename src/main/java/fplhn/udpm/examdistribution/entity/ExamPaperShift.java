package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamShiftStatus;
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
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicUpdate
@Table(name = "exam_paper_shift")
public class ExamPaperShift extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_exam_paper")
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "id_exam_shift")
    private ExamShift examShift;

    @Column(name = "start_time")
    private Long startTime;

    @Column(name = "end_time")
    private Long endTime;

    @Column(name = "exam_shift_status")
    private ExamShiftStatus examShiftStatus;

//    @Column(name = "hash")
//    private String hash;
//
//    @Column(name = "salt")
//    private String salt;

    @Column(name = "password")
    private String password;

}
