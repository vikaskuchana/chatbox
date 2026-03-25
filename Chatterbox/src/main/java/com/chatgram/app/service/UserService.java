package com.chatgram.app.service;

import com.chatgram.app.dto.AuthRequest;
import com.chatgram.app.dto.AuthResponse;
import com.chatgram.app.dto.UserDTO;
import com.chatgram.app.entity.User;
import com.chatgram.app.repository.UserRepository;
import com.chatgram.app.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtUtil;
    private final AuditService auditService;

    public AuthResponse register(AuthRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getUsername() + "@telegram.com")
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getUsername())
                .build();

        user = userRepository.save(user);

        auditService.logAction((long)user.getId(), "USER_REGISTER", "User", String.valueOf(user.getId()), null);

        String token = jwtUtil.generateToken(user.getUsername(), (long)user.getId());

        return new AuthResponse(
                token,
                token, // In production, generate separate refresh token
                convertToDTO(user)
        );
    }

    public AuthResponse login(AuthRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        auditService.logAction((long)user.getId(), "USER_LOGIN", "User", String.valueOf(user.getId()), null);

        String token = jwtUtil.generateToken(user.getUsername(), (long)user.getId());

        return new AuthResponse(token, token, convertToDTO(user));
    }

    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToDTO(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                new ArrayList<>()
        );
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .id((long)user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}