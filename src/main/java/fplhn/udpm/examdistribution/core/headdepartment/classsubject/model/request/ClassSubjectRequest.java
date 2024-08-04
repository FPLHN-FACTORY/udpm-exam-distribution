package fplhn.udpm.examdistribution.core.headdepartment.classsubject.model.request;

import fplhn.udpm.examdistribution.core.common.base.PageableRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassSubjectRequest extends PageableRequest {

    private String facilityChildId;

    private String subjectName;

    private String staffName;

    private Long startDate;

    private Long endDate;

    private String shift;

    private String classSubjectCode;

    private String currentFacilityId;

    private String currentDepartmentFacilityId;

}
