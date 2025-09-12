package com.virtualgarage.auth.service;

import com.virtualgarage.auth.dto.AuthenticationRequest;
import com.virtualgarage.auth.dto.AuthenticationResponse;
import com.virtualgarage.auth.dto.RegisterRequest;
import com.virtualgarage.common.entity.User;
import com.virtualgarage.common.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    
    @Transactional
    public AuthenticationResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());
        
        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User already exists with email: " + request.getEmail());
        }
        
        // Create new user
        User user = User.builder()
                .email(request.getEmail())
                .name(request.getName())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(User.UserRole.USER)
                .status(User.UserStatus.ACTIVE)
                .emailVerified(false)
                .build();
        
        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        return buildAuthenticationResponse(savedUser, accessToken, refreshToken);
    }
    
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        log.info("Authenticating user with email: {}", request.getEmail());
        
        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        
        // Load user and update last login
        User user = userRepository.findActiveUserByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
        
        // Generate tokens
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        
        log.info("User authenticated successfully: {}", user.getId());
        return buildAuthenticationResponse(user, accessToken, refreshToken);
    }
    
    public void logout(String token) {
        log.info("Logging out user");
        jwtService.blacklistToken(token);
    }
    
    public AuthenticationResponse refreshToken(String refreshToken) {
        log.info("Refreshing access token");
        
        if (!jwtService.isValidRefreshToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }
        
        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        
        User user = userRepository.findActiveUserByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        String newAccessToken = jwtService.generateAccessToken(userDetails);
        
        return buildAuthenticationResponse(user, newAccessToken, refreshToken);
    }
    
    private AuthenticationResponse buildAuthenticationResponse(User user, String accessToken, String refreshToken) {
        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .user(AuthenticationResponse.UserDto.builder()
                        .id(user.getId())
                        .email(user.getEmail())
                        .name(user.getName())
                        .avatar(user.getAvatar())
                        .role(user.getRole().name())
                        .createdAt(user.getCreatedAt())
                        .lastLogin(user.getLastLogin())
                        .build())
                .build();
    }
}