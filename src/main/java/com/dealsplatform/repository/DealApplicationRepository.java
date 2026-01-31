package com.dealsplatform.repository;

import com.dealsplatform.entity.ApplicationStatus;
import com.dealsplatform.entity.DealApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealApplicationRepository extends JpaRepository<DealApplication, Long> {
    
    List<DealApplication> findByDealId(Long dealId);
    
    List<DealApplication> findByDealIdAndStatus(Long dealId, ApplicationStatus status);
    
    Optional<DealApplication> findByApplicantIdAndDealId(Long applicantId, Long dealId);
    
    boolean existsByDealIdAndApplicantIdAndStatus(Long dealId, Long applicantId, ApplicationStatus status);
    
    List<DealApplication> findByApplicantId(Long applicantId);
    
    Optional<DealApplication> findByIdAndDealId(Long applicationId, Long dealId);
}
