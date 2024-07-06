package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.uploadexampaper.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ListExamPaperResponse {

    private String id;

    private String subjectId;

    private String subjectName;

    private String majorName;

    private String examPaperCode;

    private String staffName;

    private long createdDate;

    private String status;

    private String facilityName;

    private String thumnailLink;

}
