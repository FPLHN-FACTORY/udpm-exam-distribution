package fplhn.udpm.examdistribution.core.teacher.examfile.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface TEListResourceExamPaperResponse extends HasOrderNumber, IsIdentify {

    String getResource();

}
