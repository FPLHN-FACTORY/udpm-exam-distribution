package fplhn.udpm.examdistribution.core.headsubject.uploadexampaper.assignuploader.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_HEAD_SUBJECT_MANAGE_UPLOAD_EXAM_PAPER)
public class AssignUploaderController {

    @GetMapping
    public String viewAssignUploader() {
        return "/head-subject/upload-exam-paper/upload-exam-paper";
    }

}
