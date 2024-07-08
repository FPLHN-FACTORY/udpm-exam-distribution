package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM)
public class JoinRoomController {

    @GetMapping
    public String viewJoinRoom() {
        return "/head-subject/join-room/join-room";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "/head-subject/room-detail/room-detail";
    }

}
