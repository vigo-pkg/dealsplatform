package com.dealsplatform.dto;

import jakarta.validation.constraints.NotNull;

public class DealVoteDto {
    
    @NotNull(message = "Голос обязателен")
    private Boolean vote; // true - выиграл, false - проиграл
    
    // Constructors
    public DealVoteDto() {}
    
    public DealVoteDto(Boolean vote) {
        this.vote = vote;
    }
    
    // Getters and Setters
    public Boolean getVote() {
        return vote;
    }
    
    public void setVote(Boolean vote) {
        this.vote = vote;
    }
}
