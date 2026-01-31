package com.dealsplatform.dto;

import com.dealsplatform.entity.ApplicationStatus;
import com.dealsplatform.entity.ApplicationType;
import java.time.LocalDateTime;

public class DealApplicationResponseDto {
    
    private Long id;
    private Long applicantId;
    private String applicantEmail;
    private ApplicationType applicationType;
    private ApplicationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime reviewedAt;
    private Long reviewedById;
    private String reviewedByEmail;
    
    public DealApplicationResponseDto() {}
    
    public DealApplicationResponseDto(Long id, Long applicantId, String applicantEmail, 
                                     ApplicationType applicationType, ApplicationStatus status,
                                     LocalDateTime createdAt, LocalDateTime updatedAt,
                                     LocalDateTime reviewedAt, Long reviewedById, String reviewedByEmail) {
        this.id = id;
        this.applicantId = applicantId;
        this.applicantEmail = applicantEmail;
        this.applicationType = applicationType;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.reviewedAt = reviewedAt;
        this.reviewedById = reviewedById;
        this.reviewedByEmail = reviewedByEmail;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public String getApplicantEmail() {
        return applicantEmail;
    }
    
    public void setApplicantEmail(String applicantEmail) {
        this.applicantEmail = applicantEmail;
    }
    
    public ApplicationType getApplicationType() {
        return applicationType;
    }
    
    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }
    
    public ApplicationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }
    
    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
    
    public Long getReviewedById() {
        return reviewedById;
    }
    
    public void setReviewedById(Long reviewedById) {
        this.reviewedById = reviewedById;
    }
    
    public String getReviewedByEmail() {
        return reviewedByEmail;
    }
    
    public void setReviewedByEmail(String reviewedByEmail) {
        this.reviewedByEmail = reviewedByEmail;
    }
}
