package fplhn.udpm.examdistribution.infrastructure.conflig.classsubjectexcelconflig.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClassSubjectExcelRequest {

    private String facilityChildName;

    private String blockName;

    private String classCode;

    private String subjectCode;

    private String date;

    private String shift;

    private String staffCode;

}
