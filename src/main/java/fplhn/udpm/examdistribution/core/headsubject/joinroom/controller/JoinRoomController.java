package fplhn.udpm.examdistribution.core.headsubject.joinroom.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM)
public class JoinRoomController {

    @GetMapping
    public String viewJoinRoom() {
        return "/head-subject/join-room/join-room";
    }

}
