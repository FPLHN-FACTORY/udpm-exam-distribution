package fplhn.udpm.examdistribution.core.student.trackstudent.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.CheckRoomIsValidRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.SaveTrackUrlRequest;

public interface StudentExamShiftTrackService {

    ResponseObject<?> checkExamShiftIsValid(CheckRoomIsValidRequest request);

    ResponseObject<?> saveTrackUrl(SaveTrackUrlRequest request);

}
