package fplhn.udpm.examdistribution.core.headsubject.subjectrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface SRExamRuleResponse extends IsIdentify, HasOrderNumber {

    String getName();

    Integer getIsChecked();

}
