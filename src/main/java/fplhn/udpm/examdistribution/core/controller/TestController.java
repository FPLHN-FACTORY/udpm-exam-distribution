package fplhn.udpm.examdistribution.core.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/giang-vien")
    public String giangVien(){
        return "giang-vien";
    }

    @GetMapping("/sinh-vien")
    public String sinhVien(){
        return "sinh-vien";
    }

}
