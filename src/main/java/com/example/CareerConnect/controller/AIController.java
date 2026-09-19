package com.example.CareerConnect.controller;

import com.example.CareerConnect.dto.AIAnalysisResponse;
import com.example.CareerConnect.entity.Job;
import com.example.CareerConnect.entity.User;
import com.example.CareerConnect.service.AIService;
import com.example.CareerConnect.service.JobMatchingService;
import com.example.CareerConnect.service.JobService;
import com.example.CareerConnect.service.ResumeParserService;
import com.example.CareerConnect.service.UserService;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.nio.file.Path;
import java.util.List;

@Controller
public class AIController {

    private final ResumeParserService resumeParserService;
    private final UserService userService;
    private final JobService jobService;
    private final JobMatchingService jobMatchingService;
    private final AIService aiService;

    public AIController(
            ResumeParserService resumeParserService,
            UserService userService,
            JobService jobService,
            JobMatchingService jobMatchingService,
            AIService aiService) {

        this.resumeParserService = resumeParserService;
        this.userService = userService;
        this.jobService = jobService;
        this.jobMatchingService = jobMatchingService;
        this.aiService = aiService;
    }

    @GetMapping("/student/ai/test/{jobId}")
    public String testResumeParser(
            @PathVariable Long jobId,
            Authentication authentication,
            Model model) throws Exception {

        // 1. Get the logged-in student
        String username = authentication.getName();

        User student = userService.findByUsername(username);

        if (student == null) {
            return "redirect:/no-role";
        }

        // 2. Get the selected job
        Job job = jobService.findJobById(jobId);

        if (job == null) {
            return "redirect:/student/dashboard";
        }

        // 3. Get the student's resume filename
        String resumeFilename = student.getResumeUrl();

        if (resumeFilename == null || resumeFilename.isBlank()) {
            return "redirect:/student/profile?error=no_resume";
        }

        // 4. Build resume path
        Path resumePath = Path.of(
                "uploads",
                String.valueOf(student.getId()),
                resumeFilename
        );

        // 5. Extract resume text
        String resumeText =
                resumeParserService.extractText(resumePath);

        // 6. Find matched skills
        List<String> matchedSkills =
                jobMatchingService.findMatchedSkills(
                        resumeText,
                        job.getSkills()
                );

        // 7. Find missing skills
        List<String> missingSkills =
                jobMatchingService.findMissingSkills(
                        resumeText,
                        job.getSkills()
                );

        // 8. Calculate deterministic match percentage
        double matchPercentage =
                jobMatchingService.calculateMatchPercentage(
                        resumeText,
                        job.getSkills()
                );

        // 9. Ask AI to analyze the resume
        String aiAnalysis =
        aiService.generateAnalysis(
                resumeText,
                job.getTitle(),
                job.getSkills(),
                matchPercentage,
                matchedSkills,
                missingSkills
        );

        // 10. Create analysis response
        AIAnalysisResponse analysis =
                new AIAnalysisResponse(
                        job.getTitle(),
                        matchPercentage,
                        matchedSkills,
                        missingSkills
                );

        // 11. Add data to Thymeleaf
        model.addAttribute("analysis", analysis);
        model.addAttribute("aiAnalysis", aiAnalysis);

        // 12. Open result page
        return "student/ai-result";
    }
}