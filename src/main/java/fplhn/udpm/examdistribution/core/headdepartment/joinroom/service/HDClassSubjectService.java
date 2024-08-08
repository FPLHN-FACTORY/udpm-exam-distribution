package fplhn.udpm.examdistribution.core.headdepartment.joinroom.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.headdepartment.joinroom.model.request.HDClassSubjectRequest;

public interface HDClassSubjectService {

    ResponseObject<?> getClassSubject(String classSubjectCode, String subjectId);

    ResponseObject<?> getClassSubjectIdByRequest(HDClassSubjectRequest hdClassSubjectRequest);

}
