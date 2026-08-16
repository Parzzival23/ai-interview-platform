package com.aiinterviewplatform.backend.service;

import com.aiinterviewplatform.backend.dto.LoginRequest;
import com.aiinterviewplatform.backend.entity.User;
import com.aiinterviewplatform.backend.exception.InvalidCredentialsException;
import com.aiinterviewplatform.backend.repository.UserRepository;
import com.aiinterviewplatform.backend.dto.RegisterRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository,PasswordEncoder passwordEncoder,JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
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
    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new InvalidCredentialsException("Invalid email or password"));
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword())) {

            throw new InvalidCredentialsException("Invalid email or password");
        }
        return jwtService.generateToken(user);
    }


}
