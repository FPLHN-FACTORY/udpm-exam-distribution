package fplhn.udpm.examdistribution.infrastructure.conflig.websocket.controller;

import fplhn.udpm.examdistribution.infrastructure.conflig.websocket.response.NotificationResponse;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ExamShiftNotificationController {

    @MessageMapping("/exam-shift-join")
    @SendTo("/topic/exam-shift")
    public NotificationResponse notifyExamShiftJoin(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/student-exam-shift-join")
    @SendTo("/topic/student-exam-shift")
    public NotificationResponse notifyStudentExamShift(String message) {
        return new NotificationResponse(message);
    }

    @MessageMapping("/exam-shift-kick")
    @SendTo("/topic/student-exam-shift-kick")
    public NotificationResponse notifyKickStudentInExamShift(String message) {
        return new NotificationResponse(message);
    }

}
