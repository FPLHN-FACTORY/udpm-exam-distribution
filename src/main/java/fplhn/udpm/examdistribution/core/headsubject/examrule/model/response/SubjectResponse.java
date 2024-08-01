package fplhn.udpm.examdistribution.core.headsubject.examrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SubjectResponse extends IsIdentify, HasOrderNumber {

    String getCode();

    String getName();

    Integer getIsChecked();

}
