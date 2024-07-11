package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.model.request;

import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
public class AssignUploaderRequest {

    private String staffId;

    private String subjectId;

    private BigInteger maxUpload;

}
