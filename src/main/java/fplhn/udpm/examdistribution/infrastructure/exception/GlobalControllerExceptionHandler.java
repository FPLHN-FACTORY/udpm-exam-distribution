package fplhn.udpm.examdistribution.infrastructure.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ModelAndView handleException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return new ModelAndView("500/index", model.asMap(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
