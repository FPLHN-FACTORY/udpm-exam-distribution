package fplhn.udpm.examdistribution.core.teacher.examshift.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TExamShiftWhenStartExamRequest {

    private String examShiftCode;

    private Long examDate;

    private String shift;

    private String subjectId;

    private String blockId;

    private String departmentFacilityId;

}
