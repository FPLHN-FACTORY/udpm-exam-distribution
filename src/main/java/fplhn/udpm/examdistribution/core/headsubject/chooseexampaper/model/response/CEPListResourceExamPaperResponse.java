package fplhn.udpm.examdistribution.core.headsubject.chooseexampaper.model.response;

import fplhn.udpm.examdistribution.core.common.base.HasOrderNumber;
import fplhn.udpm.examdistribution.core.common.base.IsIdentify;

public interface CEPListResourceExamPaperResponse extends HasOrderNumber, IsIdentify {

    String getResource();

}
