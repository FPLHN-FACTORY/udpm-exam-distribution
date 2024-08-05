package fplhn.udpm.examdistribution.core.headoffice.examrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HOSubjectResponse extends IsIdentify, HasOrderNumber {

    String getCode();

    String getName();

    Integer getIsChecked();

}
