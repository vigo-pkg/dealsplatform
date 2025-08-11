package com.dealsplatform.repository;

import com.dealsplatform.entity.Deal;
import com.dealsplatform.entity.DealStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DealRepository extends JpaRepository<Deal, Long> {
    
    List<Deal> findByStatus(DealStatus status);
    
    List<Deal> findByCreatorId(Long creatorId);
    
    @Query("SELECT d FROM Deal d WHERE d.status = :status AND d.endTime <= :now")
    List<Deal> findExpiredDeals(@Param("status") DealStatus status, @Param("now") LocalDateTime now);
    
    @Query("SELECT d FROM Deal d WHERE d.status = :status AND d.startTime <= :now AND d.endTime > :now")
    List<Deal> findActiveDeals(@Param("status") DealStatus status, @Param("now") LocalDateTime now);
}
