package com.example.CareerConnect.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AIService {

    private final RestTemplate restTemplate = new RestTemplate();

    public String generateAnalysis(
            String resumeText,
            String jobTitle,
            String jobSkills,
            double matchPercentage,
            List<String> matchedSkills,
            List<String> missingSkills) {

        String prompt = """
                You are an AI career assistant inside a job portal.

                Analyze the candidate's resume against the selected job.

                Job Title:
                %s

                Required Skills:
                %s

                Deterministic Resume Match Score:
                %.2f%%

                Backend Matched Skills:
                %s

                Backend Missing Skills:
                %s

                Candidate Resume:
                %s

                Provide a concise analysis containing:

                1. Overall Assessment
                2. Strong Matching Skills
                3. Missing or Weak Skills
                4. Specific Recommendations to Improve the Resume
                5. Recommended Topics to Learn for This Job

                Important rules:
                - The backend matched and missing skill lists are authoritative.
                - Do not contradict the backend matched skill list.
                - Do not say a skill is missing if it appears in Backend Matched Skills.
                - Do not say a skill is matched if it appears in Backend Missing Skills.
                - Do not invent skills, projects, experience, or qualifications.
                - Treat the deterministic match score as provided.
                - Give practical recommendations for a student.
                - Do not use Markdown.
                - Do not use asterisks.
                - Do not use hashtags.
                - Use simple numbered lists or bullet points.
                - Keep the response concise and easy to read.
                """.formatted(
                jobTitle,
                jobSkills,
                matchPercentage,
                String.join(", ", matchedSkills),
                String.join(", ", missingSkills),
                resumeText
        );

        Map<String, Object> request = Map.of(
                "model", "llama3.2",
                "prompt", prompt,
                "stream", false
        );

        Map<String, Object> response =
                restTemplate.postForObject(
                        "http://localhost:11434/api/generate",
                        request,
                        Map.class
                );

        if (response == null || response.get("response") == null) {
            return "No AI analysis was generated.";
        }

        return response.get("response").toString();
    }
}