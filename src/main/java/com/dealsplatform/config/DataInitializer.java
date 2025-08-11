package com.dealsplatform.config;

import com.dealsplatform.entity.Deal;
import com.dealsplatform.entity.DealParticipant;
import com.dealsplatform.entity.DealObserver;
import com.dealsplatform.entity.User;
import com.dealsplatform.repository.DealObserverRepository;
import com.dealsplatform.repository.DealParticipantRepository;
import com.dealsplatform.repository.DealRepository;
import com.dealsplatform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DealRepository dealRepository;
    
    @Autowired
    private DealParticipantRepository participantRepository;
    
    @Autowired
    private DealObserverRepository observerRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Создаем тестовых пользователей
        if (userRepository.count() == 0) {
            createTestUsers();
        }
        
        // Создаем тестовые пари
        if (dealRepository.count() == 0) {
            createTestDeals();
        }
    }
    
    private void createTestUsers() {
        User user1 = new User();
        user1.setEmail("alice@example.com");
        user1.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user1);
        
        User user2 = new User();
        user2.setEmail("bob@example.com");
        user2.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user2);
        
        User user3 = new User();
        user3.setEmail("charlie@example.com");
        user3.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user3);
        
        User user4 = new User();
        user4.setEmail("diana@example.com");
        user4.setPassword(passwordEncoder.encode("password123"));
        userRepository.save(user4);
        
        System.out.println("Создано 4 тестовых пользователя");
    }
    
    private void createTestDeals() {
        User alice = userRepository.findByEmail("alice@example.com").orElse(null);
        User bob = userRepository.findByEmail("bob@example.com").orElse(null);
        User charlie = userRepository.findByEmail("charlie@example.com").orElse(null);
        User diana = userRepository.findByEmail("diana@example.com").orElse(null);
        
        if (alice != null && bob != null && charlie != null && diana != null) {
            // Пари 1: Открытое пари
            Deal deal1 = new Deal();
            deal1.setDescription("Кто больше отжиманий сделает за 1 минуту?");
            deal1.setStartTime(LocalDateTime.now().plusHours(1));
            deal1.setDurationMinutes(60);
            deal1.setCreator(alice);
            deal1.setStatus(com.dealsplatform.entity.DealStatus.OPEN);
            deal1 = dealRepository.save(deal1);
            
            // Создатель автоматически становится участником
            DealParticipant creator1 = new DealParticipant(deal1, alice, true);
            participantRepository.save(creator1);
            
            // Пари 2: В процессе
            Deal deal2 = new Deal();
            deal2.setDescription("Кто быстрее решит 10 математических задач?");
            deal2.setStartTime(LocalDateTime.now().minusMinutes(30));
            deal2.setDurationMinutes(120);
            deal2.setCreator(bob);
            deal2.setStatus(com.dealsplatform.entity.DealStatus.IN_PROGRESS);
            deal2 = dealRepository.save(deal2);
            
            DealParticipant creator2 = new DealParticipant(deal2, bob, true);
            participantRepository.save(creator2);
            
            DealParticipant participant2 = new DealParticipant(deal2, charlie, false);
            participantRepository.save(participant2);
            
            // Пари 3: Завершено, доступно голосование
            Deal deal3 = new Deal();
            deal3.setDescription("Кто больше слов напишет за 5 минут?");
            deal3.setStartTime(LocalDateTime.now().minusHours(2));
            deal3.setDurationMinutes(5);
            deal3.setCreator(diana);
            deal3.setStatus(com.dealsplatform.entity.DealStatus.IMPLEMENTED);
            deal3 = dealRepository.save(deal3);
            
            DealParticipant creator3 = new DealParticipant(deal3, diana, true);
            participantRepository.save(creator3);
            
            DealParticipant participant3 = new DealParticipant(deal3, alice, false);
            participantRepository.save(participant3);
            
            // Наблюдатель для третьего пари
            DealObserver observer3 = new DealObserver(deal3, bob);
            observerRepository.save(observer3);
            
            System.out.println("Создано 3 тестовых пари");
        }
    }
}
