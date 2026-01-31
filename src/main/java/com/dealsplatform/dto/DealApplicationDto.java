package com.dealsplatform.dto;

import com.dealsplatform.entity.ApplicationType;
import jakarta.validation.constraints.NotNull;

public class DealApplicationDto {
    
    @NotNull(message = "Тип заявки обязателен")
    private ApplicationType applicationType;
    
    public DealApplicationDto() {}
    
    public DealApplicationDto(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }
    
    public ApplicationType getApplicationType() {
        return applicationType;
    }
    
    public void setApplicationType(ApplicationType applicationType) {
        this.applicationType = applicationType;
    }
}
