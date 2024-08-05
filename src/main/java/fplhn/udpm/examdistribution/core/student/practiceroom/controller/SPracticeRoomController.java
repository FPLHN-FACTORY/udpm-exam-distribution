package fplhn.udpm.examdistribution.core.student.practiceroom.controller;

import ch.qos.logback.core.model.Model;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_STUDENT_PRACTICE_ROOM)
public class SPracticeRoomController {

    @GetMapping
    public String practiceRoom() {
        return "student/practice-room/practice-room";
    }

}
