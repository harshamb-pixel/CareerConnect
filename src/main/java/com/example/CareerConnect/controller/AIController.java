package com.example.CareerConnect.controller;

import com.example.CareerConnect.service.ResumeParserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.nio.file.Path;

@Controller
public class AIController {

    private final ResumeParserService resumeParserService;

    public AIController(ResumeParserService resumeParserService) {
        this.resumeParserService = resumeParserService;
    }

    @GetMapping("/student/ai/test")
    public String testResumeParser() throws Exception {

        Path resumePath = Path.of(
                "uploads/2/Harsha_MB_resume (6).pdf"
        );

        String resumeText = resumeParserService.extractText(resumePath);

        System.out.println("========== RESUME TEXT ==========");
        System.out.println(resumeText);
        System.out.println("=================================");

        return "redirect:/student/dashboard";
    }
}