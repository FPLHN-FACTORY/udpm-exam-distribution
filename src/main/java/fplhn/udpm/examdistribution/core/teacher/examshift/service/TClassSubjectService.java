package fplhn.udpm.examdistribution.core.teacher.examshift.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.examshift.model.request.TClassSubjectRequest;

public interface TClassSubjectService {

    ResponseObject<?> getClassSubjectIdByRequest(TClassSubjectRequest tClassSubjectRequest);

}
