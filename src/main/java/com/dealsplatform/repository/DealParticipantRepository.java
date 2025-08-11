package com.dealsplatform.repository;

import com.dealsplatform.entity.DealParticipant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealParticipantRepository extends JpaRepository<DealParticipant, Long> {
    
    List<DealParticipant> findByDealId(Long dealId);
    
    Optional<DealParticipant> findByDealIdAndParticipantId(Long dealId, Long participantId);
    
    boolean existsByDealIdAndParticipantId(Long dealId, Long participantId);
    
    long countByDealId(Long dealId);
}
