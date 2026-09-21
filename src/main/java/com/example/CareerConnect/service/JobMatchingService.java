
package com.example.CareerConnect.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
public class JobMatchingService {

    public List<String> extractSkills(String skillsText) {

        if (skillsText == null || skillsText.isBlank()) {
            return new ArrayList<>();
        }

        return Arrays.stream(skillsText.split(","))
                .map(String::trim)
                .filter(skill -> !skill.isBlank())
                .toList();
    }

    public List<String> findMatchedSkills(
            String resumeText,
            String jobSkills) {

        List<String> requiredSkills = extractSkills(jobSkills);

        List<String> matchedSkills = new ArrayList<>();

        String normalizedResume = resumeText
                .toLowerCase(Locale.ROOT);

        for (String skill : requiredSkills) {

            String normalizedSkill = skill
                    .toLowerCase(Locale.ROOT)
                    .trim();

            if (normalizedResume.contains(normalizedSkill)) {
                matchedSkills.add(skill);
            }
        }

        return matchedSkills;
    }

    public List<String> findMissingSkills(
            String resumeText,
            String jobSkills) {

        List<String> requiredSkills = extractSkills(jobSkills);

        List<String> missingSkills = new ArrayList<>();

        String normalizedResume = resumeText
                .toLowerCase(Locale.ROOT);

        for (String skill : requiredSkills) {

            String normalizedSkill = skill
                    .toLowerCase(Locale.ROOT)
                    .trim();

            if (!normalizedResume.contains(normalizedSkill)) {
                missingSkills.add(skill);
            }
        }

        return missingSkills;
    }

    public double calculateMatchPercentage(
            String resumeText,
            String jobSkills) {

        List<String> requiredSkills = extractSkills(jobSkills);

        if (requiredSkills.isEmpty()) {
            return 0.0;
        }

        List<String> matchedSkills =
                findMatchedSkills(resumeText, jobSkills);

        return (matchedSkills.size() * 100.0)
                / requiredSkills.size();
    }
}
