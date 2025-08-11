package com.dealsplatform.service;

import com.dealsplatform.dto.AuthResponseDto;
import com.dealsplatform.dto.UserLoginDto;
import com.dealsplatform.dto.UserRegistrationDto;
import com.dealsplatform.entity.User;
import com.dealsplatform.repository.UserRepository;
import com.dealsplatform.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    public AuthResponseDto registerUser(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }
        
        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        String token = jwtTokenProvider.generateToken(savedUser.getEmail());
        
        return new AuthResponseDto(token, savedUser.getEmail(), savedUser.getId());
    }
    
    public AuthResponseDto loginUser(UserLoginDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword())
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        User user = userRepository.findByEmail(loginDto.getEmail())
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
        
        String token = jwtTokenProvider.generateToken(user.getEmail());
        
        return new AuthResponseDto(token, user.getEmail(), user.getId());
    }
    
    public User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    
    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("Пользователь не найден"));
    }
}
