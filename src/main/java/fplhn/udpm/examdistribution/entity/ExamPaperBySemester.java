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
@ToString
@DynamicUpdate
@Table(name = "exam_paper_by_semester")
public class ExamPaperBySemester extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_exam_paper")
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "id_semester")
    private Semester semester;

}
