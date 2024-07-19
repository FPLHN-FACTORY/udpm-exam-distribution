package fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.controller;

import fplhn.udpm.examdistribution.core.headdepartment.joinroom.examshift.service.HDExamShiftService;
import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_JOIN_ROOM)
public class HDExamShiftController {

    private final HDExamShiftService hdExamShiftService;

    @GetMapping
    public String viewJoinRoom() {
        return "head-department/join-room/room";
    }

    @GetMapping("/{examShiftCode}")
    public String viewDetail(@PathVariable String examShiftCode, Model model) {
        if (!hdExamShiftService.getExamShiftByRequest(examShiftCode)) {
            return "error/404";
        }
        model.addAttribute("examShiftCodeCtl", examShiftCode);
        return "head-department/room-detail/room-detail";
    }

}
