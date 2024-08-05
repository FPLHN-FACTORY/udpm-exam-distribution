package fplhn.udpm.examdistribution.core.headoffice.examrule.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface HOExamRuleResponse extends IsIdentify, HasOrderNumber {

    String getName();

    String getCode();

    String getFileId();

    Integer getStatus();

}
