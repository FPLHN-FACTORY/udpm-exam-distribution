package fplhn.udpm.examdistribution.core.headoffice.block.model.response;

import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface BlockResponse extends IsIdentify {

    String getSemesterId();

    String getBlockName();

    Long getStartTime();

    Long getEndTime();

    Integer getBlockStatus();

}
