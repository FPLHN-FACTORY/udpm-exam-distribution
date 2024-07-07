package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM)
public class HDExamShiftController {

    @GetMapping
    public String viewJoinRoom() {
        return "/head-department/join-room/join-room";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "/head-department/room-detail/room-detail";
    }

}
