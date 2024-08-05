package fplhn.udpm.examdistribution.core.teacher.mockexampaper.service;

import fplhn.udpm.examdistribution.core.common.base.ResponseObject;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TMEPStudentRequest;
import fplhn.udpm.examdistribution.core.teacher.mockexampaper.model.request.TPracticeRoomRequest;

public interface TMEPPracticeRoomService {

    ResponseObject<?> createPracticeRoom(TPracticeRoomRequest practiceRoom);

    ResponseObject<?> detailPracticeRoom(String practiceRoomId);

    void clearPracticeRoom();

    ResponseObject<?> getListStudentJoin(TMEPStudentRequest studentRequest);

}
