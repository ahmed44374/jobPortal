package com.luv2code.jobportal.controller;

import com.luv2code.jobportal.entity.*;
import com.luv2code.jobportal.services.JobPostActivityService;
import com.luv2code.jobportal.services.JobSeekerApplyService;
import com.luv2code.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService, JobSeekerApplyService jobSeekerApplyService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
    }

    @GetMapping("/dashboard/")
    public String searchJobs(Model model, @RequestParam(value = "job",required = false)String job,
                             @RequestParam(value = "location",required = false) String location,
                             @RequestParam(value = "partTime",required = false) String partTime,
                             @RequestParam(value = "fullTime",required = false) String fullTime,
                             @RequestParam(value = "freelance",required = false) String freelance,
                             @RequestParam(value = "remoteOnly",required = false) String remoteOnly,
                             @RequestParam(value = "officeOnly",required = false) String officeOnly,
                             @RequestParam(value = "partialTime",required = false) String partialTime,
                             @RequestParam(value = "partialRemote",required = false) String partialRemote,
                             @RequestParam(value = "today",required = false) boolean today,
                             @RequestParam(value = "days7",required = false) boolean days7,
                             @RequestParam(value = "days30",required = false) boolean days30
                             ) {
        model.addAttribute("partTime", Objects.equals(partialTime,"Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime,"Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance,"Freelance"));

        model.addAttribute("remoteOnly", Objects.equals(remoteOnly,"Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly,"Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote,"Partial-Remote"));

        model.addAttribute("today",today);
        model.addAttribute("days7",days7);
        model.addAttribute("days30",days30);

        model.addAttribute("job",job);
        model.addAttribute("location",location);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        if(days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        }else if(today) {
            searchDate = LocalDate.now();
        }else {
            dateSearchFlag = false;
        }

        if(partialTime==null && fullTime == null && freelance == null) {
            partTime = "Part-Time";
            fullTime = "Full-Time";
            freelance = "Freelance";
            remote = false;
        }

        if(officeOnly == null && remoteOnly == null && partialRemote == null) {
            officeOnly = "Office-Only";
            remoteOnly = "Remote-Only";
            partialRemote = "Partial-Remote";
            type = false;
        }
        if(!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)) {
            jobPost = jobPostActivityService.getAll();
        }else {
            jobPost =  jobPostActivityService.search(job,location, Arrays.asList(partTime,fullTime,freelance),Arrays.asList(remoteOnly,officeOnly,partialRemote),searchDate);
        }

        Object currentUserProfile = usersService.getCurrentUserProfile();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            model.addAttribute("username", currentUsername);
            if (authentication.getAuthorities().contains(new SimpleGrantedAuthority("Recruiter"))) {
                List<RecruiterJobsDto> recruiterJobs = jobPostActivityService.getRecruiterJobs(((RecruiterProfile) currentUserProfile).getUserAccountId());
                model.addAttribute("jobPost", recruiterJobs);
            }else {
                jobSeekerApplyService.getCandidatesJobs((JobSeekerProfile) currentUserProfile)
            }
        }

        model.addAttribute("user", currentUserProfile);

        return "dashboard";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model) {
        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model) {

        Users user = usersService.getCurrentUser();
        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        JobPostActivity saved = jobPostActivityService.addNew(jobPostActivity);
        return "redirect:/dashboard/";
    }

    @PostMapping("/dashboard/deleteJob/{id}")
    public String deletePost(@PathVariable int id){
        jobPostActivityService.deleteJob(id);
        return "redirect:/dashboard/";
    }


}








