package fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.core.headsubject.joinroom.examshift.service.HSExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_JOIN_ROOM)
public class HSExamShiftController {

    private final HSExamShiftService hsExamShiftService;

    @GetMapping
    public String viewJoinRoom() {
        return "head-subject/join-room/join-room";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        if (!hsExamShiftService.getExamShiftByRequest(examShiftCode)) {
            return "error/404";
        }
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "head-subject/room-detail/room-detail";
    }

}
