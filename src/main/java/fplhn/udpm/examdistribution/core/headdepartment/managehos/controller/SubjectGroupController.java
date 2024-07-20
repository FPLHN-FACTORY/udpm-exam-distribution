package fplhn.udpm.examdistribution.core.headdepartment.managehos.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_SUBJECT_GROUP)
public class SubjectGroupController {

    @GetMapping
    public String viewHos() {
        return "head-department/manage-subject-group/manage-subject-group";
    }

}