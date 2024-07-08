package fplhn.udpm.examdistribution.infrastructure.config.job.classsubject.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubjectSearchParams {

    private String idBlock;

    private String idFacilityChild;

    private String idSubject;

    private String idStaff;

    private Long date;

    private String shift;

    private String classCode;

}
