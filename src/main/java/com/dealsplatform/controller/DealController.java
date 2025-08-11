package com.dealsplatform.controller;

import com.dealsplatform.dto.DealCreateDto;
import com.dealsplatform.dto.DealResponseDto;
import com.dealsplatform.dto.DealVoteDto;
import com.dealsplatform.dto.ObserverDecisionDto;
import com.dealsplatform.entity.DealStatus;
import com.dealsplatform.service.DealService;
import com.dealsplatform.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deals")
@Tag(name = "Пари", description = "API для работы с пари")
@CrossOrigin(origins = "*")
public class DealController {
    
    @Autowired
    private DealService dealService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    @Operation(summary = "Создание пари", description = "Создает новое пари")
    public ResponseEntity<DealResponseDto> createDeal(@Valid @RequestBody DealCreateDto createDto) {
        Long currentUserId = userService.getCurrentUser().getId();
        DealResponseDto response = dealService.createDeal(createDto, currentUserId);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    @Operation(summary = "Получение всех пари", description = "Возвращает список всех пари в системе")
    public ResponseEntity<List<DealResponseDto>> getAllDeals() {
        List<DealResponseDto> deals = dealService.getAllDeals();
        return ResponseEntity.ok(deals);
    }
    
    @GetMapping("/{dealId}")
    @Operation(summary = "Получение пари по ID", description = "Возвращает детальную информацию о пари")
    public ResponseEntity<DealResponseDto> getDealById(@PathVariable Long dealId) {
        DealResponseDto deal = dealService.getDealById(dealId);
        return ResponseEntity.ok(deal);
    }
    
    @GetMapping("/status/{status}")
    @Operation(summary = "Получение пари по статусу", description = "Возвращает список пари с указанным статусом")
    public ResponseEntity<List<DealResponseDto>> getDealsByStatus(@PathVariable DealStatus status) {
        List<DealResponseDto> deals = dealService.getDealsByStatus(status);
        return ResponseEntity.ok(deals);
    }
    
    @PostMapping("/{dealId}/join/participant")
    @Operation(summary = "Присоединение как участник", description = "Присоединяет текущего пользователя как участника пари")
    public ResponseEntity<DealResponseDto> joinAsParticipant(@PathVariable Long dealId) {
        Long currentUserId = userService.getCurrentUser().getId();
        DealResponseDto response = dealService.joinAsParticipant(dealId, currentUserId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{dealId}/join/observer")
    @Operation(summary = "Присоединение как наблюдатель", description = "Присоединяет текущего пользователя как наблюдателя пари")
    public ResponseEntity<DealResponseDto> joinAsObserver(@PathVariable Long dealId) {
        Long currentUserId = userService.getCurrentUser().getId();
        DealResponseDto response = dealService.joinAsObserver(dealId, currentUserId);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{dealId}/vote")
    @Operation(summary = "Голосование", description = "Голосование участника пари")
    public ResponseEntity<DealResponseDto> vote(@PathVariable Long dealId, @Valid @RequestBody DealVoteDto voteDto) {
        Long currentUserId = userService.getCurrentUser().getId();
        DealResponseDto response = dealService.vote(dealId, currentUserId, voteDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{dealId}/observer-decision")
    @Operation(summary = "Решение наблюдателя", description = "Финальное решение наблюдателя при конфликте")
    public ResponseEntity<DealResponseDto> makeObserverDecision(
            @PathVariable Long dealId, 
            @Valid @RequestBody ObserverDecisionDto decisionDto) {
        Long currentUserId = userService.getCurrentUser().getId();
        DealResponseDto response = dealService.makeObserverDecision(dealId, currentUserId, decisionDto);
        return ResponseEntity.ok(response);
    }
}
