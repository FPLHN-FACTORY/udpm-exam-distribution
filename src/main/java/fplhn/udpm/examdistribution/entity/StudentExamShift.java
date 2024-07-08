package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamStudentStatus;
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
@Table(name = "student_exam_shift")
public class StudentExamShift extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_student")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "id_exam_shift")
    private ExamShift examShift;

    @Column(name = "exam_student_status")
    private ExamStudentStatus examStudentStatus;

    @Column(name = "join_time")
    private Long joinTime;

    @Column(name = "reason")
    private String reason;

}
