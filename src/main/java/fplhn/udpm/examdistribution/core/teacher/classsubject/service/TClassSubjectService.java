package fplhn.udpm.examdistribution.core.teacher.classsubject.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.classsubject.model.request.TClassSubjectRequest;

public interface TClassSubjectService {

    ResponseObject<?> getClassSubjectIdByRequest(TClassSubjectRequest tClassSubjectRequest);

}
