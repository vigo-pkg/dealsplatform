package com.dealsplatform.repository;

import com.dealsplatform.entity.DealVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DealVoteRepository extends JpaRepository<DealVote, Long> {
    
    List<DealVote> findByDealId(Long dealId);
    
    Optional<DealVote> findByDealIdAndVoterId(Long dealId, Long voterId);
    
    boolean existsByDealIdAndVoterId(Long dealId, Long voterId);
    
    long countByDealId(Long dealId);
}
