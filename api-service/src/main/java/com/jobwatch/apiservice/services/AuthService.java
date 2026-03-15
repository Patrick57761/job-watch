package com.jobwatch.apiservice.services;

import com.jobwatch.apiservice.models.User;
import com.jobwatch.apiservice.repositories.UserRepository;
import com.jobwatch.apiservice.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String register(String email, String password) {
        // Check if email already exists
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }

        // Hash the password — never store plain text
        String hashedPassword = passwordEncoder.encode(password);

        // Create and save the user
        User user = new User();
        user.setEmail(email);
        user.setPassword(hashedPassword);
        userRepository.save(user);

        // Return a JWT token
        return jwtUtil.generateToken(email);
    }

    public String login(String email, String password) {
        // Find user by email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        // Check password matches
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // Return a JWT token
        return jwtUtil.generateToken(email);
    }
}