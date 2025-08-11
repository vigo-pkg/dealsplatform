package com.dealsplatform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal_participants")
public class DealParticipant {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participant_id", nullable = false)
    private User participant;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "is_creator")
    private Boolean isCreator = false;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
    
    // Constructors
    public DealParticipant() {}
    
    public DealParticipant(Deal deal, User participant, Boolean isCreator) {
        this.deal = deal;
        this.participant = participant;
        this.isCreator = isCreator;
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
    
    public User getParticipant() {
        return participant;
    }
    
    public void setParticipant(User participant) {
        this.participant = participant;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public Boolean getIsCreator() {
        return isCreator;
    }
    
    public void setIsCreator(Boolean isCreator) {
        this.isCreator = isCreator;
    }
}
