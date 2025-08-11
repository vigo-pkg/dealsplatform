package com.dealsplatform.service;

import com.dealsplatform.dto.DealCreateDto;
import com.dealsplatform.dto.DealResponseDto;
import com.dealsplatform.dto.DealVoteDto;
import com.dealsplatform.dto.ObserverDecisionDto;
import com.dealsplatform.entity.*;
import com.dealsplatform.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealService {
    
    @Autowired
    private DealRepository dealRepository;
    
    @Autowired
    private DealParticipantRepository participantRepository;
    
    @Autowired
    private DealObserverRepository observerRepository;
    
    @Autowired
    private DealVoteRepository voteRepository;
    
    @Autowired
    private UserService userService;
    
    @Transactional
    public DealResponseDto createDeal(DealCreateDto createDto, Long creatorId) {
        User creator = userService.getUserById(creatorId);
        
        Deal deal = new Deal();
        deal.setDescription(createDto.getDescription());
        deal.setStartTime(createDto.getStartTime());
        deal.setDurationMinutes(createDto.getDurationMinutes());
        deal.setCreator(creator);
        
        Deal savedDeal = dealRepository.save(deal);
        
        // Создатель автоматически становится участником
        DealParticipant creatorParticipant = new DealParticipant(savedDeal, creator, true);
        participantRepository.save(creatorParticipant);
        
        return convertToResponseDto(savedDeal);
    }
    
    @Transactional
    public DealResponseDto joinAsParticipant(Long dealId, Long userId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        if (deal.getStatus() != DealStatus.OPEN) {
            throw new RuntimeException("Нельзя присоединиться к пари в статусе: " + deal.getStatus());
        }
        
        User user = userService.getUserById(userId);
        
        if (participantRepository.existsByDealIdAndParticipantId(dealId, userId)) {
            throw new RuntimeException("Пользователь уже является участником этого пари");
        }
        
        DealParticipant participant = new DealParticipant(deal, user, false);
        participantRepository.save(participant);
        
        // Проверяем, нужно ли изменить статус
        checkAndUpdateDealStatus(deal);
        
        return convertToResponseDto(deal);
    }
    
    @Transactional
    public DealResponseDto joinAsObserver(Long dealId, Long userId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        if (deal.getStatus() == DealStatus.RESOLVED) {
            throw new RuntimeException("Нельзя присоединиться к уже разрешенному пари");
        }
        
        User user = userService.getUserById(userId);
        
        if (observerRepository.existsByDealIdAndObserverId(dealId, userId)) {
            throw new RuntimeException("Пользователь уже является наблюдателем этого пари");
        }
        
        if (participantRepository.existsByDealIdAndParticipantId(dealId, userId)) {
            throw new RuntimeException("Участник пари не может быть наблюдателем");
        }
        
        DealObserver observer = new DealObserver(deal, user);
        observerRepository.save(observer);
        
        return convertToResponseDto(deal);
    }
    
    @Transactional
    public DealResponseDto vote(Long dealId, Long userId, DealVoteDto voteDto) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        if (deal.getStatus() != DealStatus.IMPLEMENTED) {
            throw new RuntimeException("Голосование доступно только для пари в статусе IMPLEMENTED");
        }
        
        User user = userService.getUserById(userId);
        
        if (!participantRepository.existsByDealIdAndParticipantId(dealId, userId)) {
            throw new RuntimeException("Только участники пари могут голосовать");
        }
        
        if (voteRepository.existsByDealIdAndVoterId(dealId, userId)) {
            throw new RuntimeException("Пользователь уже проголосовал");
        }
        
        DealVote vote = new DealVote(deal, user, voteDto.getVote());
        voteRepository.save(vote);
        
        System.out.println("Пользователь " + user.getEmail() + 
                          " проголосовал в пари " + dealId + 
                          ": " + (voteDto.getVote() ? "выиграл" : "проиграл"));
        
        // Проверяем результаты голосования
        checkVotingResults(deal);
        
        return convertToResponseDto(deal);
    }
    
    @Transactional
    public DealResponseDto makeObserverDecision(Long dealId, Long observerId, ObserverDecisionDto decisionDto) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        if (deal.getStatus() != DealStatus.CONFLICT) {
            throw new RuntimeException("Решение наблюдателя возможно только при конфликте");
        }
        
        DealObserver observer = observerRepository.findByDealIdAndObserverId(dealId, observerId)
            .orElseThrow(() -> new RuntimeException("Пользователь не является наблюдателем этого пари"));
        
        observer.setFinalDecision(decisionDto.getDecision());
        observer.setDecisionAt(LocalDateTime.now());
        observerRepository.save(observer);
        
        DealStatus oldStatus = deal.getStatus();
        deal.setStatus(DealStatus.RESOLVED);
        dealRepository.save(deal);
        
        System.out.println("Наблюдатель " + observer.getObserver().getEmail() + 
                          " разрешил конфликт в пари " + dealId + 
                          ": решение = " + (decisionDto.getDecision() ? "выиграл" : "проиграл") + 
                          ", статус изменен с " + oldStatus + " на " + DealStatus.RESOLVED);
        
        return convertToResponseDto(deal);
    }
    
    public List<DealResponseDto> getAllDeals() {
        return dealRepository.findAll().stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    public DealResponseDto getDealById(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        return convertToResponseDto(deal);
    }
    
    public List<DealResponseDto> getDealsByStatus(DealStatus status) {
        return dealRepository.findByStatus(status).stream()
            .map(this::convertToResponseDto)
            .collect(Collectors.toList());
    }
    
    @Scheduled(fixedRate = 60000) // Каждую минуту
    @Transactional
    public void checkExpiredDeals() {
        LocalDateTime now = LocalDateTime.now();
        List<Deal> expiredDeals = dealRepository.findExpiredDeals(DealStatus.IN_PROGRESS, now);
        
        for (Deal deal : expiredDeals) {
            deal.setStatus(DealStatus.IMPLEMENTED);
            dealRepository.save(deal);
        }
    }
    
    private void checkAndUpdateDealStatus(Deal deal) {
        long participantCount = participantRepository.countByDealId(deal.getId());
        if (participantCount >= 2 && deal.getStatus() == DealStatus.OPEN) {
            deal.setStatus(DealStatus.IN_PROGRESS);
            dealRepository.save(deal);
        }
    }
    
    private void checkVotingResults(Deal deal) {
        List<DealVote> votes = voteRepository.findByDealId(deal.getId());
        long participantCount = participantRepository.countByDealId(deal.getId());
        
        if (votes.size() == participantCount) {
            // Все участники проголосовали
            boolean allWon = votes.stream().allMatch(v -> v.getVote());
            boolean allLost = votes.stream().allMatch(v -> !v.getVote());
            
            DealStatus oldStatus = deal.getStatus();
            DealStatus newStatus;
            
            if (allLost) {
                // Все участники считают, что проиграли - пари разрешено
                newStatus = DealStatus.RESOLVED;
                System.out.println("Пари " + deal.getId() + ": Все участники проиграли - статус RESOLVED");
            } else if (allWon) {
                // Все участники считают, что выиграли - КОНФЛИКТ!
                // Логически невозможно, чтобы оба выиграли
                newStatus = DealStatus.CONFLICT;
                System.out.println("Пари " + deal.getId() + ": Все участники выиграли - КОНФЛИКТ (логически невозможно)");
            } else {
                // Разные мнения - конфликт
                newStatus = DealStatus.CONFLICT;
                System.out.println("Пари " + deal.getId() + ": Разные мнения участников - КОНФЛИКТ");
            }
            
            deal.setStatus(newStatus);
            dealRepository.save(deal);
            
            System.out.println("Пари " + deal.getId() + ": Статус изменен с " + oldStatus + " на " + newStatus);
        }
    }
    
    private DealResponseDto convertToResponseDto(Deal deal) {
        DealResponseDto dto = new DealResponseDto();
        dto.setId(deal.getId());
        dto.setDescription(deal.getDescription());
        dto.setStartTime(deal.getStartTime());
        dto.setDurationMinutes(deal.getDurationMinutes());
        dto.setStatus(deal.getStatus());
        dto.setCreatorId(deal.getCreator().getId());
        dto.setCreatorEmail(deal.getCreator().getEmail());
        dto.setCreatedAt(deal.getCreatedAt());
        dto.setUpdatedAt(deal.getUpdatedAt());
        dto.setEndTime(deal.getEndTime());
        
        // Участники
        List<DealResponseDto.ParticipantDto> participants = participantRepository.findByDealId(deal.getId())
            .stream()
            .map(p -> new DealResponseDto.ParticipantDto(
                p.getParticipant().getId(),
                p.getParticipant().getEmail(),
                p.getJoinedAt(),
                p.getIsCreator()
            ))
            .collect(Collectors.toList());
        dto.setParticipants(participants);
        
        // Наблюдатели
        List<DealResponseDto.ObserverDto> observers = observerRepository.findByDealId(deal.getId())
            .stream()
            .map(o -> new DealResponseDto.ObserverDto(
                o.getObserver().getId(),
                o.getObserver().getEmail(),
                o.getJoinedAt(),
                o.getFinalDecision(),
                o.getDecisionAt()
            ))
            .collect(Collectors.toList());
        dto.setObservers(observers);
        
        // Голоса
        List<DealResponseDto.VoteDto> votes = voteRepository.findByDealId(deal.getId())
            .stream()
            .map(v -> new DealResponseDto.VoteDto(
                v.getId(),
                v.getVoter().getEmail(),
                v.getVote(),
                v.getVotedAt()
            ))
            .collect(Collectors.toList());
        dto.setVotes(votes);
        
        return dto;
    }
}
