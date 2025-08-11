package com.dealsplatform.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Email
    @Column(unique = true, nullable = false)
    private String email;
    
    @NotBlank
    @Column(nullable = false)
    private String password;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL)
    private Set<Deal> createdDeals = new HashSet<>();
    
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL)
    private Set<DealParticipant> participations = new HashSet<>();
    
    @OneToMany(mappedBy = "observer", cascade = CascadeType.ALL)
    private Set<DealObserver> observations = new HashSet<>();
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Constructors
    public User() {}
    
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    
    public Set<Deal> getCreatedDeals() {
        return createdDeals;
    }
    
    public void setCreatedDeals(Set<Deal> createdDeals) {
        this.createdDeals = createdDeals;
    }
    
    public Set<DealParticipant> getParticipations() {
        return participations;
    }
    
    public void setParticipations(Set<DealParticipant> participations) {
        this.participations = participations;
    }
    
    public Set<DealObserver> getObservations() {
        return observations;
    }
    
    public void setObservations(Set<DealObserver> observations) {
        this.observations = observations;
    }
}
