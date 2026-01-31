package com.dealsplatform.service;

import com.dealsplatform.dto.*;
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
    private DealApplicationRepository applicationRepository;
    
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
        
        return convertToResponseDto(savedDeal, creatorId);
    }
    
    @Transactional
    public DealResponseDto createApplication(Long dealId, Long userId, DealApplicationDto applicationDto) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        if (applicationDto.getApplicationType() == ApplicationType.PARTICIPANT) {
            if (deal.getStatus() != DealStatus.OPEN) {
                throw new RuntimeException("Нельзя подать заявку на участие в пари в статусе: " + deal.getStatus());
            }
        } else if (applicationDto.getApplicationType() == ApplicationType.OBSERVER) {
            if (deal.getStatus() == DealStatus.RESOLVED) {
                throw new RuntimeException("Нельзя подать заявку на наблюдение за уже разрешенным пари");
            }
        }
        
        User user = userService.getUserById(userId);
        
        // Проверяем, нет ли уже активной заявки
        if (applicationRepository.existsByDealIdAndApplicantIdAndStatus(dealId, userId, ApplicationStatus.PENDING)) {
            throw new RuntimeException("У вас уже есть активная заявка на это пари");
        }
        
        // Проверяем, не является ли уже участником/наблюдателем
        if (applicationDto.getApplicationType() == ApplicationType.PARTICIPANT) {
            if (participantRepository.existsByDealIdAndParticipantId(dealId, userId)) {
                throw new RuntimeException("Вы уже являетесь участником этого пари");
            }
        } else {
            if (observerRepository.existsByDealIdAndObserverId(dealId, userId)) {
                throw new RuntimeException("Вы уже являетесь наблюдателем этого пари");
            }
            if (participantRepository.existsByDealIdAndParticipantId(dealId, userId)) {
                throw new RuntimeException("Участник пари не может быть наблюдателем");
            }
        }
        
        DealApplication application = new DealApplication(deal, user, applicationDto.getApplicationType());
        applicationRepository.save(application);
        
        return convertToResponseDto(deal, userId);
    }
    
    @Deprecated
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
        
        return convertToResponseDto(deal, userId);
    }
    
    @Deprecated
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
        
        return convertToResponseDto(deal, userId);
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
        
        return convertToResponseDto(deal, userId);
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
        
        return convertToResponseDto(deal, observerId);
    }
    
    public List<DealResponseDto> getAllDeals() {
        return dealRepository.findAll().stream()
            .map(deal -> convertToResponseDto(deal, null))
            .collect(Collectors.toList());
    }
    
    public DealResponseDto getDealById(Long dealId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        return convertToResponseDto(deal, null);
    }
    
    public DealResponseDto getDealById(Long dealId, Long currentUserId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        return convertToResponseDto(deal, currentUserId);
    }
    
    public List<DealApplicationResponseDto> getApplicationsByDealId(Long dealId, Long currentUserId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        // Только создатель может просматривать заявки
        if (!deal.getCreator().getId().equals(currentUserId)) {
            throw new RuntimeException("Только создатель пари может просматривать заявки");
        }
        
        return applicationRepository.findByDealId(dealId).stream()
            .map(this::convertApplicationToDto)
            .collect(Collectors.toList());
    }
    
    public List<DealApplicationResponseDto> getPendingApplicationsByDealId(Long dealId, Long currentUserId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        // Только создатель может просматривать заявки
        if (!deal.getCreator().getId().equals(currentUserId)) {
            throw new RuntimeException("Только создатель пари может просматривать заявки");
        }
        
        return applicationRepository.findByDealIdAndStatus(dealId, ApplicationStatus.PENDING).stream()
            .map(this::convertApplicationToDto)
            .collect(Collectors.toList());
    }
    
    @Transactional
    public DealResponseDto approveApplication(Long dealId, Long applicationId, Long creatorId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        // Проверяем, что пользователь является создателем
        if (!deal.getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("Только создатель пари может одобрять заявки");
        }
        
        DealApplication application = applicationRepository.findByIdAndDealId(applicationId, dealId)
            .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Можно одобрить только заявки со статусом PENDING");
        }
        
        User creator = userService.getUserById(creatorId);
        application.setStatus(ApplicationStatus.APPROVED);
        application.setReviewedBy(creator);
        applicationRepository.save(application);
        
        // Создаем участника или наблюдателя
        if (application.getApplicationType() == ApplicationType.PARTICIPANT) {
            if (participantRepository.existsByDealIdAndParticipantId(dealId, application.getApplicant().getId())) {
                throw new RuntimeException("Пользователь уже является участником этого пари");
            }
            DealParticipant participant = new DealParticipant(deal, application.getApplicant(), false);
            participantRepository.save(participant);
            
            // Проверяем, нужно ли изменить статус
            checkAndUpdateDealStatus(deal);
        } else if (application.getApplicationType() == ApplicationType.OBSERVER) {
            if (observerRepository.existsByDealIdAndObserverId(dealId, application.getApplicant().getId())) {
                throw new RuntimeException("Пользователь уже является наблюдателем этого пари");
            }
            if (participantRepository.existsByDealIdAndParticipantId(dealId, application.getApplicant().getId())) {
                throw new RuntimeException("Участник пари не может быть наблюдателем");
            }
            DealObserver observer = new DealObserver(deal, application.getApplicant());
            observerRepository.save(observer);
        }
        
        return convertToResponseDto(deal, creatorId);
    }
    
    @Transactional
    public DealResponseDto rejectApplication(Long dealId, Long applicationId, Long creatorId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        // Проверяем, что пользователь является создателем
        if (!deal.getCreator().getId().equals(creatorId)) {
            throw new RuntimeException("Только создатель пари может отклонять заявки");
        }
        
        DealApplication application = applicationRepository.findByIdAndDealId(applicationId, dealId)
            .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Можно отклонить только заявки со статусом PENDING");
        }
        
        User creator = userService.getUserById(creatorId);
        application.setStatus(ApplicationStatus.REJECTED);
        application.setReviewedBy(creator);
        applicationRepository.save(application);
        
        return convertToResponseDto(deal, creatorId);
    }
    
    @Transactional
    public DealResponseDto withdrawApplication(Long dealId, Long applicationId, Long userId) {
        Deal deal = dealRepository.findById(dealId)
            .orElseThrow(() -> new RuntimeException("Пари не найдено"));
        
        DealApplication application = applicationRepository.findByIdAndDealId(applicationId, dealId)
            .orElseThrow(() -> new RuntimeException("Заявка не найдена"));
        
        // Проверяем, что пользователь является автором заявки
        if (!application.getApplicant().getId().equals(userId)) {
            throw new RuntimeException("Только автор заявки может её отозвать");
        }
        
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new RuntimeException("Можно отозвать только заявки со статусом PENDING");
        }
        
        application.setStatus(ApplicationStatus.WITHDRAWN);
        applicationRepository.save(application);
        
        return convertToResponseDto(deal, userId);
    }
    
    public List<DealResponseDto> getDealsByStatus(DealStatus status) {
        return dealRepository.findByStatus(status).stream()
            .map(deal -> convertToResponseDto(deal, null))
            .collect(Collectors.toList());
    }
    
    @Scheduled(fixedRate = 60000) // Каждую минуту
    @Transactional
    public void checkExpiredDeals() {
        LocalDateTime now = LocalDateTime.now();
        
        // Проверяем пари, которые должны перейти из OPEN в IN_PROGRESS
        List<Deal> dealsToStart = dealRepository.findAll().stream()
            .filter(d -> d.getStatus() == DealStatus.OPEN 
                     && d.getStartTime() != null 
                     && !d.getStartTime().isAfter(now)
                     && participantRepository.countByDealId(d.getId()) >= 2)
            .collect(Collectors.toList());
        
        for (Deal deal : dealsToStart) {
            System.out.println("⏰ Пари " + deal.getId() + ": Переход из OPEN в IN_PROGRESS (время старта наступило)");
            deal.setStatus(DealStatus.IN_PROGRESS);
            dealRepository.save(deal);
        }
        
        // Проверяем пари со статусом IN_PROGRESS, у которых истекло время
        List<Deal> expiredDeals = dealRepository.findExpiredDeals(DealStatus.IN_PROGRESS, now);
        
        for (Deal deal : expiredDeals) {
            System.out.println("⏰ Пари " + deal.getId() + ": Время истекло, переход в IMPLEMENTED");
            System.out.println("   Описание: " + deal.getDescription());
            System.out.println("   Время окончания: " + deal.getEndTime());
            System.out.println("   Текущее время: " + now);
            deal.setStatus(DealStatus.IMPLEMENTED);
            dealRepository.save(deal);
        }
        
        // Также проверяем пари в статусе OPEN, у которых уже истекло время (на случай, если они не перешли в IN_PROGRESS)
        List<Deal> openExpiredDeals = dealRepository.findAll().stream()
            .filter(d -> d.getStatus() == DealStatus.OPEN 
                     && d.getEndTime() != null 
                     && !d.getEndTime().isAfter(now))
            .collect(Collectors.toList());
        
        for (Deal deal : openExpiredDeals) {
            System.out.println("⏰ Пари " + deal.getId() + ": Время истекло (статус был OPEN), переход в IMPLEMENTED");
            deal.setStatus(DealStatus.IMPLEMENTED);
            dealRepository.save(deal);
        }
        
        if (expiredDeals.isEmpty() && dealsToStart.isEmpty() && openExpiredDeals.isEmpty()) {
            System.out.println("✅ Проверка истекших пари: изменений нет");
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
    
    private DealResponseDto convertToResponseDto(Deal deal, Long currentUserId) {
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
        
        // Ожидающие заявки (только для создателя)
        if (currentUserId != null && deal.getCreator().getId().equals(currentUserId)) {
            List<DealApplicationResponseDto> pendingApplications = applicationRepository
                .findByDealIdAndStatus(deal.getId(), ApplicationStatus.PENDING)
                .stream()
                .map(this::convertApplicationToDto)
                .collect(Collectors.toList());
            dto.setPendingApplications(pendingApplications);
        }
        
        // Информация о заявке текущего пользователя (если есть)
        if (currentUserId != null) {
            applicationRepository.findByApplicantIdAndDealId(currentUserId, deal.getId())
                .ifPresent(app -> {
                    dto.setUserApplication(convertApplicationToDto(app));
                });
        }
        
        return dto;
    }
    
    private DealApplicationResponseDto convertApplicationToDto(DealApplication application) {
        DealApplicationResponseDto dto = new DealApplicationResponseDto();
        dto.setId(application.getId());
        dto.setApplicantId(application.getApplicant().getId());
        dto.setApplicantEmail(application.getApplicant().getEmail());
        dto.setApplicationType(application.getApplicationType());
        dto.setStatus(application.getStatus());
        dto.setCreatedAt(application.getCreatedAt());
        dto.setUpdatedAt(application.getUpdatedAt());
        dto.setReviewedAt(application.getReviewedAt());
        if (application.getReviewedBy() != null) {
            dto.setReviewedById(application.getReviewedBy().getId());
            dto.setReviewedByEmail(application.getReviewedBy().getEmail());
        }
        return dto;
    }
}
