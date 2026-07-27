package com.example.CareerConnect.controller;

import com.example.CareerConnect.entity.Job;
import com.example.CareerConnect.service.JobService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public String listJobs(Model model, @RequestParam(value = "keyword", required = false) String keyword) {
        List<Job> jobs = jobService.searchJobs(keyword);
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        return "jobs";
    }
}
