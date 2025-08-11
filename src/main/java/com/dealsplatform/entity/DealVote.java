package com.dealsplatform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal_votes")
public class DealVote {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;
    
    @Column(nullable = false)
    private Boolean vote; // true - выиграл, false - проиграл
    
    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;
    
    @PrePersist
    protected void onCreate() {
        votedAt = LocalDateTime.now();
    }
    
    // Constructors
    public DealVote() {}
    
    public DealVote(Deal deal, User voter, Boolean vote) {
        this.deal = deal;
        this.voter = voter;
        this.vote = vote;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Deal getDeal() {
        return deal;
    }
    
    public void setDeal(Deal deal) {
        this.deal = deal;
    }
    
    public User getVoter() {
        return voter;
    }
    
    public void setVoter(User voter) {
        this.voter = voter;
    }
    
    public Boolean getVote() {
        return vote;
    }
    
    public void setVote(Boolean vote) {
        this.vote = vote;
    }
    
    public LocalDateTime getVotedAt() {
        return votedAt;
    }
    
    public void setVotedAt(LocalDateTime votedAt) {
        this.votedAt = votedAt;
    }
}
