package com.dealsplatform.dto;

import com.dealsplatform.entity.DealStatus;
import java.time.LocalDateTime;
import java.util.List;

public class DealResponseDto {
    
    private Long id;
    private String description;
    private LocalDateTime startTime;
    private Integer durationMinutes;
    private DealStatus status;
    private Long creatorId;
    private String creatorEmail;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime endTime;
    private List<ParticipantDto> participants;
    private List<ObserverDto> observers;
    private List<VoteDto> votes;
    
    // Constructors
    public DealResponseDto() {}
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    
    public DealStatus getStatus() {
        return status;
    }
    
    public void setStatus(DealStatus status) {
        this.status = status;
    }
    
    public Long getCreatorId() {
        return creatorId;
    }
    
    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }
    
    public String getCreatorEmail() {
        return creatorEmail;
    }
    
    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
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
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public List<ParticipantDto> getParticipants() {
        return participants;
    }
    
    public void setParticipants(List<ParticipantDto> participants) {
        this.participants = participants;
    }
    
    public List<ObserverDto> getObservers() {
        return observers;
    }
    
    public void setObservers(List<ObserverDto> observers) {
        this.observers = observers;
    }
    
    public List<VoteDto> getVotes() {
        return votes;
    }
    
    public void setVotes(List<VoteDto> votes) {
        this.votes = votes;
    }
    
    // Inner DTOs
    public static class ParticipantDto {
        private Long id;
        private String email;
        private LocalDateTime joinedAt;
        private Boolean isCreator;
        
        public ParticipantDto() {}
        
        public ParticipantDto(Long id, String email, LocalDateTime joinedAt, Boolean isCreator) {
            this.id = id;
            this.email = email;
            this.joinedAt = joinedAt;
            this.isCreator = isCreator;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public LocalDateTime getJoinedAt() { return joinedAt; }
        public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
        
        public Boolean getIsCreator() { return isCreator; }
        public void setIsCreator(Boolean isCreator) { this.isCreator = isCreator; }
    }
    
    public static class ObserverDto {
        private Long id;
        private String email;
        private LocalDateTime joinedAt;
        private Boolean finalDecision;
        private LocalDateTime decisionAt;
        
        public ObserverDto() {}
        
        public ObserverDto(Long id, String email, LocalDateTime joinedAt, Boolean finalDecision, LocalDateTime decisionAt) {
            this.id = id;
            this.email = email;
            this.joinedAt = joinedAt;
            this.finalDecision = finalDecision;
            this.decisionAt = decisionAt;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public LocalDateTime getJoinedAt() { return joinedAt; }
        public void setJoinedAt(LocalDateTime joinedAt) { this.joinedAt = joinedAt; }
        
        public Boolean getFinalDecision() { return finalDecision; }
        public void setFinalDecision(Boolean finalDecision) { this.finalDecision = finalDecision; }
        
        public LocalDateTime getDecisionAt() { return decisionAt; }
        public void setDecisionAt(LocalDateTime decisionAt) { this.decisionAt = decisionAt; }
    }
    
    public static class VoteDto {
        private Long id;
        private String voterEmail;
        private Boolean vote;
        private LocalDateTime votedAt;
        
        public VoteDto() {}
        
        public VoteDto(Long id, String voterEmail, Boolean vote, LocalDateTime votedAt) {
            this.id = id;
            this.voterEmail = voterEmail;
            this.vote = vote;
            this.votedAt = votedAt;
        }
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public String getVoterEmail() { return voterEmail; }
        public void setVoterEmail(String voterEmail) { this.voterEmail = voterEmail; }
        
        public Boolean getVote() { return vote; }
        public void setVote(Boolean vote) { this.vote = vote; }
        
        public LocalDateTime getVotedAt() { return votedAt; }
        public void setVotedAt(LocalDateTime votedAt) { this.votedAt = votedAt; }
    }
}
