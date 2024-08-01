package fplhn.udpm.examdistribution.core.headsubject.examrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface ExamRuleResponse extends IsIdentify, HasOrderNumber {

    String getName();

    String getCode();

    String getFileId();

    Integer getStatus();

}
