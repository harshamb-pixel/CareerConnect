
package com.example.CareerConnect.dto;

import java.util.List;

public class AIAnalysisResponse {

    private String jobTitle;

    private double matchPercentage;

    private List<String> matchedSkills;

    private List<String> missingSkills;

    public AIAnalysisResponse() {
    }

    public AIAnalysisResponse(
            String jobTitle,
            double matchPercentage,
            List<String> matchedSkills,
            List<String> missingSkills) {

        this.jobTitle = jobTitle;
        this.matchPercentage = matchPercentage;
        this.matchedSkills = matchedSkills;
        this.missingSkills = missingSkills;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public double getMatchPercentage() {
        return matchPercentage;
    }

    public void setMatchPercentage(double matchPercentage) {
        this.matchPercentage = matchPercentage;
    }

    public List<String> getMatchedSkills() {
        return matchedSkills;
    }

    public void setMatchedSkills(List<String> matchedSkills) {
        this.matchedSkills = matchedSkills;
    }

    public List<String> getMissingSkills() {
        return missingSkills;
    }

    public void setMissingSkills(List<String> missingSkills) {
        this.missingSkills = missingSkills;
    }
}
