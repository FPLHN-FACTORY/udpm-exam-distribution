package fplhn.udpm.examdistribution.core.headsubject.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headsubject.joinroom.model.request.HSClassSubjectRequest;

public interface HSClassSubjectService {

    ResponseObject<?> getClassSubject(String classSubjectCode, String subjectId);

    ResponseObject<?> getClassSubjectIdByRequest(HSClassSubjectRequest hsClassSubjectRequest);

}
