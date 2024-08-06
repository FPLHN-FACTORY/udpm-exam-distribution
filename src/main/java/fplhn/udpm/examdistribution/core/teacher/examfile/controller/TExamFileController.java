package fplhn.udpm.examdistribution.core.teacher.examfile.controller;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.SessionConstant;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MappingConstants.REDIRECT_TEACHER_EXAM_FILE)
@RequiredArgsConstructor
public class TExamFileController {

    private final HttpSession httpSession;

    @GetMapping
    public String viewExamFile() {
        String isAssignUploader = (String)httpSession.getAttribute(SessionConstant.CURRENT_USER_IS_ASSIGN_UPLOADER);
        if (isAssignUploader.equals("TRUE")){
            return "teacher/manage-exam-file/exam-file";
        }else {
            return "error/403";
        }
    }

    @GetMapping("/subject/{id}")
    public String viewDetailExamFile(@PathVariable("id") String id) {
        return "teacher/manage-exam-file/upload-exam-file";
    }

    @GetMapping("/create-exam-paper/{subjectId}")
    public String viewCreateExamPaper() {
        return "teacher/manage-exam-file/create-exam-paper";
    }

}
