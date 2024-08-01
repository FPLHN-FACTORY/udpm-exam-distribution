package fplhn.udpm.examdistribution.core.headdepartment.headsubjects.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_DEPARTMENT_MANAGE_HEAD_SUBJECTS)
public class HeadSubjectsController {

    @GetMapping
    public String getHeadSubjects() {
        return "head-department/manage-head-of-subjects/manage-head-of-subjects";
    }

}
