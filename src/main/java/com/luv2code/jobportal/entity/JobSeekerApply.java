package com.luv2code.jobportal.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.config.annotation.authentication.configuration.EnableGlobalAuthentication;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(uniqueConstraints = {
        @UniqueConstraint(columnNames = {"userId","job"})
})
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JobSeekerApply implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "userId",referencedColumnName = "user_account_id")
    private JobSeekerProfile userId;
    @JoinColumn(name = "job",referencedColumnName = "jobPostId")
    @ManyToOne(cascade = CascadeType.ALL)
    private JobPostActivity job;
    private Date applyDate;
    private String coverLetter;
}
