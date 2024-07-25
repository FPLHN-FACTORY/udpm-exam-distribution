package fplhn.udpm.examdistribution.infrastructure.config.websocket.controller;

import fplhn.udpm.examdistribution.infrastructure.config.websocket.response.NotificationResponse;
import fplhn.udpm.examdistribution.infrastructure.constant.TopicConstant;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ExamShiftNotificationController {

    @MessageMapping("/exam-shift-join")
    @SendTo(TopicConstant.TOPIC_EXAM_SHIFT)
    public NotificationResponse notifyExamShiftJoin(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/student-exam-shift-join")
    @SendTo(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT)
    public NotificationResponse notifyStudentExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-kick")
    @SendTo(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_KICK)
    public NotificationResponse notifyKickStudentInExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-rejoin")
    @SendTo(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REJOIN)
    public NotificationResponse notifyStudentRejoinExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-approve")
    @SendTo(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_APPROVE)
    public NotificationResponse notifyApproveStudentExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-refuse")
    @SendTo(TopicConstant.TOPIC_STUDENT_EXAM_SHIFT_REFUSE)
    public NotificationResponse notifyRefuseStudentExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-head-subject-join")
    @SendTo(TopicConstant.TOPIC_HEAD_SUBJECT_JOIN_EXAM_SHIFT)
    public NotificationResponse notifyExamShiftHeadSubjectJoin(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-head-department-join")
    @SendTo(TopicConstant.TOPIC_HEAD_DEPARTMENT_JOIN_EXAM_SHIFT)
    public NotificationResponse notifyExamShiftHeadDepartmentJoin(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-start")
    @SendTo(TopicConstant.TOPIC_EXAM_SHIFT_START)
    public NotificationResponse notifyExamShiftStart(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/track-student")
    @SendTo(TopicConstant.TOPIC_TRACK_STUDENT)
    public NotificationResponse notifyTrackStudent(String message) {
        return new NotificationResponse(message);
    }

}
