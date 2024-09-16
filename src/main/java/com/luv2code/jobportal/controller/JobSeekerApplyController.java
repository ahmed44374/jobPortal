package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.JobPostActivity;
import com.luv2code.jobportal.services.JobPostActivityService;
import com.luv2code.jobportal.services.JobSeekerApplyService;
import com.luv2code.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class JobSeekerApplyController {
    private final JobPostActivityService jobPostActivityService;
    private final UsersService usersService;

    private final JobSeekerApplyService jobSeekerApplyService;


    @Autowired
    public JobSeekerApplyController(JobPostActivityService jobPostActivityService, UsersService usersService, JobSeekerApplyService jobSeekerApplyService) {
        this.jobPostActivityService = jobPostActivityService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
    }
    @GetMapping("job-details-apply/{id}")
    public String display(@PathVariable("id") int id, Model model) {
        JobPostActivity jobDetails = jobPostActivityService.getOne(id);
        model.addAttribute("jobDetails",jobDetails);
        model.addAttribute("user",usersService.getCurrentUserProfile());
        return "job-details";
    }
    @PostMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id,Model model) {
        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity",jobPostActivity);
        model.addAttribute("user",usersService.getCurrentUserProfile());
        return "add-jobs";
    }

}
