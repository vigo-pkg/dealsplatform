package com.dealsplatform.dto;

import jakarta.validation.constraints.NotNull;

public class ObserverDecisionDto {
    
    @NotNull(message = "Решение обязательно")
    private Boolean decision; // true - первая сторона выиграла, false - вторая
    
    // Constructors
    public ObserverDecisionDto() {}
    
    public ObserverDecisionDto(Boolean decision) {
        this.decision = decision;
    }
    
    // Getters and Setters
    public Boolean getDecision() {
        return decision;
    }
    
    public void setDecision(Boolean decision) {
        this.decision = decision;
    }
}
