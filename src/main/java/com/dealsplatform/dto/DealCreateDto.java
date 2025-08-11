package com.dealsplatform.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;

public class DealCreateDto {
    
    @NotBlank(message = "Описание пари обязательно")
    private String description;
    
    @NotNull(message = "Время старта обязательно")
    private LocalDateTime startTime;
    
    @NotNull(message = "Длительность обязательна")
    @Positive(message = "Длительность должна быть положительной")
    private Integer durationMinutes;
    
    // Constructors
    public DealCreateDto() {}
    
    public DealCreateDto(String description, LocalDateTime startTime, Integer durationMinutes) {
        this.description = description;
        this.startTime = startTime;
        this.durationMinutes = durationMinutes;
    }
    
    // Getters and Setters
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
}
