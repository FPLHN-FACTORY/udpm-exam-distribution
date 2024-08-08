package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UEPSharePermissionExamPaperRequest {

    private String examPaperId;

    private String[] listStaffId;

}
