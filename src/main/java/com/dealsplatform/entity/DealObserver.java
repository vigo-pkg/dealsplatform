package com.dealsplatform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "deal_observers")
public class DealObserver {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "deal_id", nullable = false)
    private Deal deal;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "observer_id", nullable = false)
    private User observer;
    
    @Column(name = "joined_at", nullable = false)
    private LocalDateTime joinedAt;
    
    @Column(name = "final_decision")
    private Boolean finalDecision; // true - первая сторона выиграла, false - вторая
    
    @Column(name = "decision_at")
    private LocalDateTime decisionAt;
    
    @PrePersist
    protected void onCreate() {
        joinedAt = LocalDateTime.now();
    }
    
    // Constructors
    public DealObserver() {}
    
    public DealObserver(Deal deal, User observer) {
        this.deal = deal;
        this.observer = observer;
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
    
    public User getObserver() {
        return observer;
    }
    
    public void setObserver(User observer) {
        this.observer = observer;
    }
    
    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }
    
    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
    
    public Boolean getFinalDecision() {
        return finalDecision;
    }
    
    public void setFinalDecision(Boolean finalDecision) {
        this.finalDecision = finalDecision;
        if (finalDecision != null) {
            this.decisionAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getDecisionAt() {
        return decisionAt;
    }
    
    public void setDecisionAt(LocalDateTime decisionAt) {
        this.decisionAt = decisionAt;
    }
}
