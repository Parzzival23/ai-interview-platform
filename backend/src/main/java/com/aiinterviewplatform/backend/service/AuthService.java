package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.repository.UserRepository;
import com.aiinterviewplatform.backend.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email already registered");
        }
        String hashedPassword = passwordEncoder.encode(request.password());
        User user = new User(
                request.name(),    // "Akshat"
                request.email(),   // "akshat@example.com"
                hashedPassword
        );
        userRepository.save(user);
    }


}
