package fplhn.udpm.examdistribution.core.headsubject.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;

public interface HSClassSubjectService {

    ResponseObject<?> getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest);

}
