package fplhn.udpm.examdistribution.core.teacher.trackhistory.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.trackhistory.model.request.ListStudentMakeMistakeRequest;

public interface TTrackHistoryService {

    ResponseObject<?> getListStudentMakeMistake(ListStudentMakeMistakeRequest request);

}
