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

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "share_permission_exam_paper")
public class SharePermissionExamPaper extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_exam_paper")
    private ExamPaper examPaper;

    @ManyToOne
    @JoinColumn(name = "id_staff")
    private Staff staff;

    @ManyToOne
    @JoinColumn(name = "id_block")
    private Block block;

    @ManyToOne
    @JoinColumn(name = "id_facility")
    private Facility facility;

}
