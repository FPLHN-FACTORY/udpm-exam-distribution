package fplhn.udpm.examdistribution.core.student.trackstudent.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.CheckRoomIsValidRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.InfoRoomRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.RemoveTabRequest;
import fplhn.udpm.examdistribution.core.student.trackstudent.model.request.SaveTrackUrlRequest;

public interface StudentExamShiftTrackService {

    ResponseObject<?> checkExamShiftIsValid(CheckRoomIsValidRequest request);

    ResponseObject<?> getExamShiftInfo(InfoRoomRequest request);

    ResponseObject<?> saveTrackUrl(SaveTrackUrlRequest request);

    ResponseObject<?> removeTab(RemoveTabRequest request);
}
