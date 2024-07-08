package fplhn.udpm.examdistribution.entity;

import fplhn.udpm.examdistribution.entity.base.PrimaryEntity;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperStatus;
import fplhn.udpm.examdistribution.infrastructure.constant.ExamPaperType;
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

import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Table(name = "exam_paper")
public class ExamPaper extends PrimaryEntity implements Serializable {

    @ManyToOne
    @JoinColumn(name = "id_subject")
    private Subject subject;

    @ManyToOne
    @JoinColumn(name = "id_major_facility")
    private MajorFacility majorFacility;

    @Column(name = "exam_paper_code")
    private String examPaperCode;

    @Column(name = "created_exam_paper_date")
    private Long examPaperCreatedDate;

    @Column(name = "exam_paper_type")
    @Enumerated(EnumType.STRING)
    private ExamPaperType examPaperType;

    @Column(name = "exam_paper_status")
    @Enumerated(EnumType.STRING)
    private ExamPaperStatus examPaperStatus;

    @Column(name = "path")
    private String path;

    @ManyToOne
    @JoinColumn(name = "id_staff_upload")
    private Staff staffUpload;

}
