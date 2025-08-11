package com.dealsplatform.repository;

import com.dealsplatform.entity.DealObserver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealObserverRepository extends JpaRepository<DealObserver, Long> {
    
    List<DealObserver> findByDealId(Long dealId);
    
    Optional<DealObserver> findByDealIdAndObserverId(Long dealId, Long observerId);
    
    boolean existsByDealIdAndObserverId(Long dealId, Long observerId);
}
